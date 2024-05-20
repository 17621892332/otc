package org.orient.otc.quote.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.dm.dto.CalendarStartEndDto;
import org.orient.otc.api.dm.dto.TradayAddDaysDto;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeRiskCacularResultSourceType;
import org.orient.otc.api.quote.enums.TradeRiskCacularResultType;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.api.quote.vo.TradeRiskPVInfoVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.TradeRiskInfoPvDTO;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.entity.TradeMng;
import org.orient.otc.quote.entity.TradeRiskInfo;
import org.orient.otc.quote.enums.OpenOrCloseEnum;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.TradeRiskInfoMapper;
import org.orient.otc.quote.service.*;
import org.orient.otc.quote.vo.AccSummaryVO;
import org.orient.otc.quote.vo.TradeRiskImportVo;
import org.orient.otc.quote.vo.TradeRiskInfoVo;
import org.orient.otc.quote.vo.daily.PositionDailyVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 日终风险实现
 */
@Service
@Slf4j
public class TradeRiskInfoServiceImpl extends ServiceImpl<BaseMapper<TradeRiskInfo>, TradeRiskInfo> implements TradeRiskInfoService {

    @Resource
    private TradeRiskInfoMapper tradeRiskInfoMapper;
    @Resource
    private SettlementService settlementService;

    @Resource
    private TradeMngService tradeMngService;
    @Resource
    private CalendarClient calendarClient;
    @Resource
    private ClientClient clientClient;
    @Resource
    private RiskService riskService;
    @Resource
    private SystemConfigUtil systemConfigUtil;
    @Resource
    private UnderlyingManagerClient underlyingManagerClient;
    @Resource
    private MarketClient marketClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TradeObsDateService tradeObsDateService;

    @Override
    public List<TradeRiskInfo> selectTradeRiskInfoListByRiskDate(Set<Integer> clientIdList, LocalDate riskDate) {
        List<TradeRiskInfo> tradeRiskInfoList = tradeRiskInfoMapper.selectList(new LambdaQueryWrapper<TradeRiskInfo>()
                .eq(TradeRiskInfo::getRiskDate, riskDate)
                .eq(TradeRiskInfo::getTradeRiskCacularResultSourceType, TradeRiskCacularResultSourceType.over)
                .eq(TradeRiskInfo::getIsDeleted, IsDeletedEnum.NO)
                .in(clientIdList != null && !clientIdList.isEmpty(), TradeRiskInfo::getClientId, clientIdList));
        if (tradeRiskInfoList.isEmpty() && systemConfigUtil.getTradeDay().equals(riskDate)){
            tradeRiskInfoList= this.selectNewTradeRiskInfoListByRiskDate(clientIdList,riskDate);
        }
        //按照交易日期倒序排序
        tradeRiskInfoList.sort(Comparator.comparing(TradeRiskInfo::getTradeDate).reversed());
        return tradeRiskInfoList;
    }

    @Override
    public List<TradeRiskInfo> selectNewTradeRiskInfoListByRiskDate(Set<Integer> clientIdList, LocalDate riskDate) {
        List<Object> otcList = stringRedisTemplate.opsForHash().values(RedisAdapter.TRADE_RISK_RESULT);
        List<TradeRiskInfo> list = JSONObject.parseArray(otcList.toString(), TradeRiskInfo.class);
        //过滤掉场内交易
        list.removeIf(item -> item.getTradeRiskCacularResultSourceType() == TradeRiskCacularResultSourceType.exchange);
        //过滤掉客户数据
        if (clientIdList==null|| clientIdList.isEmpty()){
            list.removeIf(item->item.getClientId()==null);
        }else {
            list.removeIf(item->!clientIdList.contains(item.getClientId()));
        }
        //获取实时保证金数据
        Map<String, BigDecimal> marginMap = settlementService.getTradeNowMargin(tradeMngService.getSurvivalTradeByTradeDayAndClient(clientIdList,riskDate));
        list.forEach(item -> item.setMargin(marginMap.getOrDefault(item.getTradeCode(), BigDecimal.ZERO)));
        return list;
    }

    @Override
    public void reSetTodayPnl(LocalDate riskDate) {
        TradayAddDaysDto tradayAddDaysDto = new TradayAddDaysDto();
        tradayAddDaysDto.setDate(riskDate);
        tradayAddDaysDto.setDays(-1);
        LocalDate lastTradeDay = calendarClient.tradeDayAddDays(tradayAddDaysDto);
        //当前交易日不允许重置
        if (riskDate.equals(systemConfigUtil.getTradeDay())){
            return;
        }
        //获取当日的风险数据
        List<TradeRiskInfo> toDayRiskList = tradeRiskInfoMapper.selectList(new LambdaQueryWrapper<TradeRiskInfo>()
                .eq(TradeRiskInfo::getRiskDate, riskDate)
                .eq(TradeRiskInfo::getIsDeleted, IsDeletedEnum.NO));
        //获取截止至前一天的累计盈亏
        List<TradeRiskInfo> lastRiskList = this.getTradeTotalPnl(riskDate, false,false);
        Map<OpenOrCloseEnum, Map<String, BigDecimal>> result = riskService.getOpenAndClose(riskDate);

        Map<String, BigDecimal> todayOpenAmountMap = result.getOrDefault(OpenOrCloseEnum.open, new HashMap<>());
        Map<String, BigDecimal> todayCloseAmountMap = result.getOrDefault(OpenOrCloseEnum.close, new HashMap<>());
        Map<String, TradeRiskInfo> tradeLastValuation;

        if (lastRiskList != null) {
            tradeLastValuation = lastRiskList.stream().collect(Collectors.toMap(TradeRiskInfo::getId, Function.identity(), (v1, v2) -> v2));
        } else {
            tradeLastValuation = new HashMap<>();
        }
        for (TradeRiskInfo tradeRiskInfo : toDayRiskList) {
            tradeRiskInfo.setLastTradeDayAvailableAmount(BigDecimal.ZERO);
            BigDecimal lastTotalPnl = BigDecimal.ZERO;
            if (tradeLastValuation.get(tradeRiskInfo.getId()) != null) {
                //昨日存续总额
                if (tradeLastValuation.get(tradeRiskInfo.getId()).getRiskDate().equals(lastTradeDay)) {
                    tradeRiskInfo.setLastTradeDayAvailableAmount(tradeLastValuation.get(tradeRiskInfo.getId()).getAvailableAmount());
                }
                //上一次累计盈亏
                lastTotalPnl = tradeLastValuation.get(tradeRiskInfo.getId()).getTotalProfitLoss();
            }
            tradeRiskInfo.setTodayOpenAmount(todayOpenAmountMap.get(tradeRiskInfo.getId()) == null ? BigDecimal.ZERO : todayOpenAmountMap.get(tradeRiskInfo.getId()));
            tradeRiskInfo.setTodayCloseAmount(todayCloseAmountMap.get(tradeRiskInfo.getId()) == null ? BigDecimal.ZERO : todayCloseAmountMap.get(tradeRiskInfo.getId()));
            tradeRiskInfo.setTodayOpenAmount(tradeRiskInfo.getTradeRiskCacularResultSourceType() == TradeRiskCacularResultSourceType.exchange && tradeRiskInfo.getBuyOrSell() == BuyOrSellEnum.sell
                    ? tradeRiskInfo.getTodayOpenAmount().negate() : tradeRiskInfo.getTodayOpenAmount());
            tradeRiskInfo.setTodayCloseAmount(tradeRiskInfo.getTradeRiskCacularResultSourceType() == TradeRiskCacularResultSourceType.exchange && tradeRiskInfo.getBuyOrSell() == BuyOrSellEnum.buy
                    ? tradeRiskInfo.getTodayCloseAmount().negate() : tradeRiskInfo.getTodayCloseAmount());
            tradeRiskInfo.setTodayProfitLoss(tradeRiskInfo.getAvailableAmount()
                    .subtract(tradeRiskInfo.getLastTradeDayAvailableAmount())
                    .subtract(tradeRiskInfo.getTodayOpenAmount())
                    .subtract(tradeRiskInfo.getTodayCloseAmount()));
            //累计盈亏=上一次累计盈亏+今日盈亏
            tradeRiskInfo.setTotalProfitLoss(lastTotalPnl.add(tradeRiskInfo.getTodayProfitLoss()));
            LambdaQueryWrapper<TradeRiskInfo> tradeRiskInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tradeRiskInfoLambdaQueryWrapper.eq(TradeRiskInfo::getId, tradeRiskInfo.getId());
            tradeRiskInfoLambdaQueryWrapper.eq(TradeRiskInfo::getRiskDate, tradeRiskInfo.getRiskDate());
            tradeRiskInfoMapper.update(tradeRiskInfo, tradeRiskInfoLambdaQueryWrapper);
        }
    }

    @Override
    @Transactional
    public Boolean reSetTodayPnl(LocalDate riskDate, Boolean isHavingNext) {
        if (isHavingNext) {
            CalendarStartEndDto calendarStartEndDto = new CalendarStartEndDto();
            LocalDate tradeDay = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash()
                    .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());

            calendarStartEndDto.setStartDate(riskDate);
            calendarStartEndDto.setEndDate(tradeDay);
            List<LocalDate> riskDateList = calendarClient.getTradeDateList(calendarStartEndDto);
            for (LocalDate localDate : riskDateList) {
                this.reSetTodayPnl(localDate);
            }
        } else {
            this.reSetTodayPnl(riskDate);
        }
        return Boolean.TRUE;

    }

    @Override
    public Boolean initTotalPnl(LocalDate initDate) {
        //获取初始化日期前一天的累计盈亏
        List<TradeRiskInfo> tradeMngList = getTradeTotalPnl(initDate,false, false);
        Map<String, String> maps = new HashMap<>();
        for (TradeRiskInfo tradeRiskInfo : tradeMngList) {
            maps.put(tradeRiskInfo.getId(), tradeRiskInfo.getTotalProfitLoss().toString());
        }
        log.info("初始化盈亏信息:{}", JSONObject.toJSONString(maps));
        stringRedisTemplate.delete(RedisAdapter.INIT_TOTAL_PNL);
        stringRedisTemplate.opsForHash().putAll(RedisAdapter.INIT_TOTAL_PNL, maps);
        return Boolean.TRUE;
    }

    @Override
    public void saveTradeRiskInfoBatch(SettlementDTO settlementDto, List<TradeRiskInfo> dbList) {
        for (TradeRiskInfo tradeRiskInfo : dbList) {
            tradeRiskInfo.setRiskDate(settlementDto.getSettlementDate());
            LambdaQueryWrapper<TradeRiskInfo> tradeRiskInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tradeRiskInfoLambdaQueryWrapper.eq(TradeRiskInfo::getId, tradeRiskInfo.getId());
            tradeRiskInfoLambdaQueryWrapper.eq(TradeRiskInfo::getRiskDate, settlementDto.getSettlementDate());
            if (Objects.isNull(tradeRiskInfoMapper.selectOne(tradeRiskInfoLambdaQueryWrapper))) {
                tradeRiskInfoMapper.insert(tradeRiskInfo);
            } else {
                tradeRiskInfoLambdaQueryWrapper.eq(TradeRiskInfo::getHandmade, Boolean.FALSE);
                tradeRiskInfoMapper.update(tradeRiskInfo, tradeRiskInfoLambdaQueryWrapper);
            }
        }
    }

    @Override
    public IPage<TradeRiskInfoVo> getRiskInfoListByPage(SettlementReportDTO dto) {
        LambdaQueryWrapper<TradeRiskInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TradeRiskInfo::getClientId, dto.getClientId());
        lambdaQueryWrapper.gt(TradeRiskInfo::getAvailableVolume, BigDecimal.ZERO);
        lambdaQueryWrapper.eq(TradeRiskInfo::getRiskDate, dto.getEndDate());
        lambdaQueryWrapper.orderByDesc(TradeRiskInfo::getTradeDate);
        IPage<TradeRiskInfo> ipage = this.page(new Page<>(dto.getPageNo(), dto.getPageSize()), lambdaQueryWrapper);
        //如果查询不到数据，并且结算日期为当前交易日时查询实时持仓
        if (ipage.getTotal()==0 && dto.getEndDate().equals(systemConfigUtil.getTradeDay())){
            List<Object> otcList = stringRedisTemplate.opsForHash().values(RedisAdapter.TRADE_RISK_RESULT);
            List<TradeRiskInfo> dataList = JSONObject.parseArray(otcList.toString(), TradeRiskInfo.class);
            dataList.removeIf(item->!dto.getClientId().equals(item.getClientId())||item.getAvailableVolume().compareTo(BigDecimal.ZERO)<=0);
            //按照交易日期倒序排序
            dataList.sort(Comparator.comparing(TradeRiskInfo::getTradeDate).reversed());
            int  pageNo = 1;
            int pageSize = 100;
            if (dto.getPageNo() != null && dto.getPageSize() != null) {
                pageNo = dto.getPageNo();
                pageSize = dto.getPageSize();
            }
            // 总条数
            int totalCount = dataList.size();
            int totalPage; // 总页数
            totalPage = totalCount / pageSize;
            if (totalCount % pageSize != 0) {
                ++totalPage;
            }
            List<TradeRiskInfo> list = dataList.stream().skip((long) (pageNo - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
            ipage = new Page<>();
            list.forEach(item -> {
                //交易数据需要由我们的方向转换为客户的方向
                item.changeDirection(item);
                item.setScale(item);
            });
            ipage.setRecords(list);
            ipage.setTotal(totalCount);
            ipage.setSize(pageSize);
            ipage.setCurrent(pageNo);
            ipage.setPages(totalPage);
        }
        return ipage.convert(item -> {
            //交易数据需要由我们的方向转换为客户的方向
            item.changeDirection(item);
            item.setScale(item);
            TradeRiskInfoVo vo = new TradeRiskInfoVo();
            BeanUtils.copyProperties(item, vo);
            vo.setBuyOrSellName(item.getBuyOrSell().getDesc());
            vo.setOptionTypeName(item.getOptionType().getDesc());
            return vo;
        });
    }



    @Override
    public List<AccSummaryVO> getAccSummaryList(SettlementReportDTO dto) {
        //查询对应客户的风险信息
        LambdaQueryWrapper<TradeRiskInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TradeRiskInfo::getClientId, dto.getClientId());
//        lambdaQueryWrapper.in(TradeRiskInfo::getOptionType, OptionTypeEnum.getAccOption());
        lambdaQueryWrapper.eq(TradeRiskInfo::getRiskDate, dto.getEndDate());
        List<TradeRiskInfo> dbList = this.list(lambdaQueryWrapper);
        List<TradeRiskInfo> accList;
        if (dbList.isEmpty()){
            if (dto.getEndDate().equals(systemConfigUtil.getTradeDay())) {
                //如果查询时间是当前交易日，并且未收盘时查询实时数据
                List<Object> otcList = stringRedisTemplate.opsForHash().values(RedisAdapter.TRADE_RISK_RESULT);
                dbList = JSONObject.parseArray(otcList.toString(), TradeRiskInfo.class);
                //过滤掉该客户的内容
                dbList = dbList.stream().filter(db -> Objects.equals(db.getClientId(), dto.getClientId())).collect(Collectors.toList());
                if (dbList.isEmpty()) {
                    return new ArrayList<>();
                }
            } else {
                return new ArrayList<>();
            }
        }
        //仅获取对应的累计期权
        accList = dbList.stream().filter(acc -> OptionTypeEnum.getAccOption().contains(acc.getOptionType())).collect(Collectors.toList());
        if (accList.isEmpty()) {
            return new ArrayList<>();
        }
        //获取对应的远期
        List<TradeRiskInfo> forwardList = dbList.stream().filter(forward -> forward.getOptionType() == OptionTypeEnum.AIForwardPricer
                && forward.getRelevanceTradeCode() != null).collect(Collectors.toList());
        return this.getAccSummaryList(accList, forwardList);
    }

    @Override
    public List<AccSummaryVO> getAccSummaryList(List<TradeRiskInfo> accList, List<TradeRiskInfo> forwardList) {
        //远期数量
        Map<String, BigDecimal> forwardVolumeMap = forwardList.stream()
                .collect(Collectors.groupingBy(TradeRiskInfo::getRelevanceTradeCode,
                        Collectors.reducing(BigDecimal.ZERO, item -> {
                            //东证买入,客户卖出 需要取反
                            if (item.getBuyOrSell() == BuyOrSellEnum.buy) {
                                return item.getAvailableVolume().negate();
                            } else {
                                return item.getAvailableVolume();
                            }
                        }, BigDecimal::add)));
        //远期数量
        Map<String, BigDecimal> forwardPnlMap = forwardList.stream()
                .collect(Collectors.groupingBy(TradeRiskInfo::getRelevanceTradeCode,
                        Collectors.reducing(BigDecimal.ZERO, a -> a.getTotalProfitLoss()
                                .subtract(a.getPositionProfitLoss()).negate(), BigDecimal::add)));
        //整理数据格式封装返回
        List<AccSummaryVO> accSummaryVOList = CglibUtil.copyList(accList, AccSummaryVO::new, (db, accSummaryVO) -> {
            accSummaryVO.setOptionTypeName(db.getOptionType().getDesc());
            accSummaryVO.setAccumulatedPosition(db.getAccumulatedPosition().negate());
            accSummaryVO.setAccumulatedPayment(db.getAccumulatedPayment().negate());
            accSummaryVO.setAccumulatedPnl(db.getAccumulatedPnl().negate());
            accSummaryVO.setTodayAccumulatedPosition(db.getTodayAccumulatedPosition().negate());
            accSummaryVO.setTodayAccumulatedPayment(db.getTodayAccumulatedPayment().negate());
            accSummaryVO.setTodayAccumulatedPnl(db.getTodayAccumulatedPnl().negate());
            accSummaryVO.setForwardVolume(forwardVolumeMap.getOrDefault(db.getTradeCode(), BigDecimal.ZERO));
            accSummaryVO.setForwardPnl(forwardPnlMap.getOrDefault(db.getTradeCode(), BigDecimal.ZERO));
        });

        accSummaryVOList.sort(Comparator.comparing(AccSummaryVO::getUnderlyingCode).thenComparing(AccSummaryVO::getOptionTypeName).thenComparing(AccSummaryVO::getStrike));
        return accSummaryVOList;
    }

    @Override
    public void saveCloseTradeTotalPnl() {
        List<TradeRiskInfo> tradeMngList = getTradeTotalPnl(systemConfigUtil.getTradeDay(),true, true);
        Map<String, String> maps = new HashMap<>();
        for (TradeRiskInfo tradeRiskInfo : tradeMngList) {
            maps.put(tradeRiskInfo.getId(), JSONObject.toJSONString(tradeRiskInfo));
        }
        //已平仓部分的风险快照
        stringRedisTemplate.delete(RedisAdapter.TOTAL_PNL_BY_CLOSED);
        stringRedisTemplate.opsForHash().putAll(RedisAdapter.TOTAL_PNL_BY_CLOSED, maps);
    }

    @Override
    public List<TradeRiskInfo> getTradeTotalPnl(LocalDate riskDate,Boolean isLive, Boolean isClose) {
        Set<String> underlyingSet= new HashSet<>();
        if (isLive){
            List<UnderlyingManagerVO> voList = underlyingManagerClient.getUnderlyingList();
            underlyingSet = voList.stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toSet());
        }

        return tradeRiskInfoMapper.selectLiveRiskInfo(riskDate,underlyingSet, isClose);
    }

    @Override
    public String importRiskInfo(MultipartFile file) throws IOException {
        List<TradeRiskImportVo> tradeRiskImportVoList = EasyExcel.read(file.getInputStream()).head(TradeRiskImportVo.class).sheet().doReadSync();
        BussinessException.E_300102.assertTrue(tradeRiskImportVoList.stream().map(TradeRiskImportVo::getRiskDate).collect(Collectors.toSet()).size() == 1, "风险信息维护必须均为同一天");
        LocalDate riskDate = tradeRiskImportVoList.get(0).getRiskDate();
        LocalDate lastTradeDay = calendarClient.getLastTradeDay(riskDate);
        //获取对应导入的交易信息
        List<TradeMng> tradeMngList = tradeMngService.getTradeMngListByTradeCodeSet(tradeRiskImportVoList.stream()
                .map(TradeRiskImportVo::getTradeCode).collect(Collectors.toSet()));
        Map<String, TradeMng> tradeMngMap = tradeMngList.stream().collect(Collectors.toMap(TradeMng::getTradeCode, Function.identity()));
        //校验是否均匹配交易
        Set<String> importTradeCodeSet = tradeRiskImportVoList.stream().map(TradeRiskImportVo::getTradeCode).collect(Collectors.toSet());
        importTradeCodeSet.removeAll(tradeMngMap.keySet());
        BussinessException.E_300102.assertTrue(importTradeCodeSet.isEmpty(),importTradeCodeSet+"找不到对应的交易记录");
        //获取对应的合约信息
        Set<String> underlyingCodeSet = tradeMngList.stream().map(TradeMng::getUnderlyingCode).collect(Collectors.toSet());
        List<UnderlyingManagerVO> underlyingManagerVOList = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet);
        Map<String, UnderlyingManagerVO> underlyingManagerVOMap = underlyingManagerVOList.stream().collect(Collectors.toMap(UnderlyingManagerVO::getUnderlyingCode, Function.identity()));
        //获取上一个交易日的风险数据
        List<TradeRiskInfo> lastRiskList = this.getTradeTotalPnl(riskDate,true, false);
        Map<String, TradeRiskInfo> lastRiskMap = lastRiskList.stream().collect(Collectors.toMap(TradeRiskInfo::getId, Function.identity(), (v1, v2) -> v2));

        //今日开平仓
        Map<OpenOrCloseEnum, Map<String, BigDecimal>> result=  riskService.getOpenAndClose(riskDate);
        Map<String, BigDecimal> todayOpenTradeAmountMap=(result.getOrDefault(OpenOrCloseEnum.open,new HashMap<>()));
        Map<String, BigDecimal> todayCloseTradeAmountMap=(result.getOrDefault(OpenOrCloseEnum.close,new HashMap<>()));
        //获取对应的收盘价信息
        Map<String, BigDecimal> closeMarketDataByDateMap = marketClient.getCloseMarketDataByDate(riskDate);
        List<TradeRiskInfo> tradeRiskInfoList = new ArrayList<>();
        for (TradeRiskImportVo importVo : tradeRiskImportVoList) {
            TradeMng tradeMng = tradeMngMap.get(importVo.getTradeCode());
            BussinessException.E_300102.assertNotNull(tradeMng, importVo.getTradeCode() + "找不到对应的交易记录");
            //校验合约收盘价是否落库
            BigDecimal closePrice = closeMarketDataByDateMap.get(tradeMng.getUnderlyingCode());
            BussinessException.E_300501.assertTrue(closePrice != null && closePrice.compareTo(BigDecimal.ZERO) > 0, tradeMng.getUnderlyingCode());
            UnderlyingManagerVO underlyingManagerVO = underlyingManagerVOMap.get(tradeMng.getUnderlyingCode());
            Integer contractSize = underlyingManagerVO.getContractSize();
            TradeRiskInfo riskInfo = CglibUtil.copy(tradeMng, TradeRiskInfo.class);
            //设置手动维护内容
            riskInfo.setId(importVo.getTradeCode());
            riskInfo.setHandmade(Boolean.TRUE);
            riskInfo.setId(tradeMng.getTradeCode());
            riskInfo.setTradeRiskCacularResultSourceType(TradeRiskCacularResultSourceType.over);
            riskInfo.setTradeRiskCacularResultType(TradeRiskCacularResultType.option);

            //风险里面买卖方向相反
            riskInfo.setBuyOrSell(tradeMng.getBuyOrSell() == BuyOrSellEnum.buy ? BuyOrSellEnum.sell : BuyOrSellEnum.buy);
            //实现盈亏
            riskInfo.setPositionProfitLoss(tradeMng.getTotalProfitLoss());
            //期权单价取反
            riskInfo.setOptionPremium(tradeMng.getOptionPremium() != null ? tradeMng.getOptionPremium().negate() : null);
            riskInfo.setTotalAmount(tradeMng.getTotalAmount().negate());
            riskInfo.setNotionalPrincipal(tradeMng.getNotionalPrincipal());
            riskInfo.setAvailableNotionalPrincipal(tradeMng.getAvailableNotionalPrincipal() == null
                    ? tradeMng.getNotionalPrincipal() : tradeMng.getAvailableNotionalPrincipal());

            //如果交易记录为成交当日，则将获取该交易的Day1Pnl
            riskInfo.setDay1PnL(riskDate.isEqual(tradeMng.getTradeDate()) ? tradeMng.getDay1PnL() : BigDecimal.ZERO);
            riskInfo.setUnderlyingName(underlyingManagerVO.getUnderlyingCode());
            riskInfo.setExchangeUnderlyingCode(underlyingManagerVO.getExchangeUnderlyingCode());
            riskInfo.setVarietyId(underlyingManagerVO.getVarietyId());
            riskInfo.setVarietyCode(underlyingManagerVO.getVarietyCode());
            riskInfo.setLastPrice(closePrice);
            //如果为交易日当天，则设置开仓金额为交易金额否则为0
            riskInfo.setTodayOpenAmount(todayOpenTradeAmountMap.get(riskInfo.getId()) == null ? BigDecimal.ZERO : todayOpenTradeAmountMap.get(riskInfo.getId()));
            riskInfo.setTodayCloseAmount(todayCloseTradeAmountMap.get(riskInfo.getId()) == null ? BigDecimal.ZERO : todayCloseTradeAmountMap.get(riskInfo.getId()));
            //导入数据赋值
            riskInfo.setRiskDate(riskDate);
            riskInfo.setAvailableAmount(importVo.getAvailableAmount());
            riskInfo.setMargin(importVo.getMargin());
            riskInfo.setDelta(importVo.getDelta());
            riskInfo.setDeltaLots(riskInfo.getDelta().divide(BigDecimal.valueOf(contractSize), 4, RoundingMode.HALF_UP));
            riskInfo.setDeltaCash(riskInfo.getDelta().multiply(closePrice).setScale(4, RoundingMode.HALF_UP));
            riskInfo.setGamma(importVo.getGamma());
            riskInfo.setGammaLots(riskInfo.getGamma().divide(BigDecimal.valueOf(contractSize), 4, RoundingMode.HALF_UP));
            riskInfo.setGammaCash(riskInfo.getGamma().multiply(closePrice).multiply(closePrice).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            riskInfo.setTheta(importVo.getTheta());
            riskInfo.setVega(importVo.getVega());
            riskInfo.setRho(importVo.getRho());
            riskInfo.setDividendRho(importVo.getDividendRho());
            TradeRiskInfo lastTradeDayRiskInfo = lastRiskMap.get(importVo.getTradeCode());
            BigDecimal lastTradeDayAvailableAmount = BigDecimal.ZERO;
            BigDecimal lastTotalPnl = BigDecimal.ZERO;
            if (Objects.nonNull(lastTradeDayRiskInfo)) {
                lastTotalPnl = lastTradeDayRiskInfo.getTotalProfitLoss();
                //获取昨日风险估值
                if (lastTradeDayRiskInfo.getRiskDate().isEqual(lastTradeDay)) {
                    lastTradeDayAvailableAmount = lastTradeDayRiskInfo.getAvailableAmount();
                }
            }
            riskInfo.setLastTradeDayAvailableAmount(lastTradeDayAvailableAmount);
            riskInfo.setTodayProfitLoss(riskInfo.getAvailableAmount()
                    .subtract(lastTradeDayAvailableAmount)
                    .subtract(riskInfo.getTodayOpenAmount())
                    .subtract(riskInfo.getTodayCloseAmount()));
            //累计盈亏
            riskInfo.setTotalProfitLoss(lastTotalPnl.add(riskInfo.getTodayProfitLoss()));
            tradeRiskInfoList.add(riskInfo);
        }
        //保存导入的风险信息
        tradeRiskInfoList.forEach(this::handmadeTradeRiskInfo);
        return "导入成功";
    }

    @Override
    public List<PositionDailyVO> getPositionDailyList(LocalDate queryDate, Boolean isNotInside, AssetTypeEnum assetType) {
        List<Integer> clientIdList = new ArrayList<>();
        if (isNotInside) {
            //如果仅获取外部客户时，则排除掉内部客户的交易
            clientIdList  = clientClient.getInsideClientIdList();
        }
        List<String> underlyingCodeList = new ArrayList<>();
        if (assetType!=null){
            List<UnderlyingManagerVO> underlyingManagerVOList=  underlyingManagerClient.getUnderlyingListByAssetType(assetType,queryDate);
            underlyingCodeList=underlyingManagerVOList.stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toList());
        }
        List<PositionDailyVO>  list= tradeRiskInfoMapper.selectPositionByDaily(queryDate,underlyingCodeList,clientIdList);
        //查询累计期权的观察日信息
        List<Integer> tradeIdList = list.stream()
                .filter(item->OptionTypeEnum.getHaveObsType().contains(item.getOptionType()))
                .map(PositionDailyVO::getId).collect(Collectors.toList());
        if (tradeIdList.isEmpty()) {
            return list;
        }
        List<TradeObsDateVO> tradeObsDateList = CglibUtil.copyList(tradeObsDateService.getTradeObsDateListByTradeIdList(tradeIdList),TradeObsDateVO::new);
        Map<Integer,List<TradeObsDateVO>> tradeObsDateMap= tradeObsDateList.stream().collect(Collectors.groupingBy(TradeObsDateVO::getTradeId));
        list.forEach(item->item.setObsDateList(tradeObsDateMap.getOrDefault(item.getId(),new ArrayList<>())));
        return list;
    }

    @Override
    public Page<PositionDailyVO> getPositionDailyByPage(LocalDate queryDate, Boolean isNotInside, AssetTypeEnum assetType, Integer pageNo, Integer pageSize) {
        Page<PositionDailyVO> page = new Page<>(pageNo,pageSize);
        List<Integer> clientIdList = new ArrayList<>();
        if (isNotInside) {
            //如果仅获取外部客户时，则排除掉内部客户的交易
            clientIdList  = clientClient.getInsideClientIdList();
        }
        List<String> underlyingCodeList = new ArrayList<>();
        if (assetType!=null){
            //获取对应资产类型的有效合约
            List<UnderlyingManagerVO> underlyingManagerVOList=  underlyingManagerClient.getUnderlyingListByAssetType(assetType,queryDate);
            underlyingCodeList=underlyingManagerVOList.stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toList());
        }
        //查询对应的持仓记录
        Page<PositionDailyVO> positionDailyVOPage = tradeRiskInfoMapper.selectPositionByDaily(page, queryDate, underlyingCodeList, clientIdList);
        //查询累计期权的观察日信息
        List<Integer> tradeIdList = positionDailyVOPage.getRecords().stream()
                .filter(item->OptionTypeEnum.getHaveObsType().contains(item.getOptionType()))
                .map(PositionDailyVO::getId).collect(Collectors.toList());
        if (tradeIdList.isEmpty()) {
            return positionDailyVOPage;
        }
        List<TradeObsDateVO> tradeObsDateList = CglibUtil.copyList(tradeObsDateService.getTradeObsDateListByTradeIdList(tradeIdList),TradeObsDateVO::new);
        Map<Integer,List<TradeObsDateVO>> tradeObsDateMap= tradeObsDateList.stream().collect(Collectors.groupingBy(TradeObsDateVO::getTradeId));
        positionDailyVOPage.getRecords().forEach(item->item.setObsDateList(tradeObsDateMap.getOrDefault(item.getId(),new ArrayList<>())));
        return positionDailyVOPage;
    }

    @Override
    public List<TradeRiskPVInfoVO> getRiskInfoListByRiskDate(Integer clientId, LocalDate riskDate) {
        LambdaQueryWrapper<TradeRiskInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeRiskInfo::getIsDeleted,IsDeletedEnum.NO);
        //存续数量大于O的交易
        queryWrapper.gt(TradeRiskInfo::getAvailableVolume,BigDecimal.ZERO);
        //场外交易
        queryWrapper.eq(TradeRiskInfo::getTradeRiskCacularResultSourceType,TradeRiskCacularResultSourceType.over);
        queryWrapper.eq(TradeRiskInfo::getRiskDate, riskDate);
        queryWrapper.eq(clientId!=null,TradeRiskInfo::getClientId,clientId);

        return this.listVo(queryWrapper, TradeRiskPVInfoVO.class);
    }

    @Override
    public IPage<TradeRiskPVInfoVO> getTradeRiskPvByPage(TradeRiskInfoPvDTO riskInfoPvDTO) {
        LambdaQueryWrapper<TradeRiskInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeRiskInfo::getIsDeleted, IsDeletedEnum.NO);
        //存续数量大于O的交易
        queryWrapper.gt(TradeRiskInfo::getAvailableVolume, BigDecimal.ZERO);
        //场外交易
        queryWrapper.eq(TradeRiskInfo::getTradeRiskCacularResultSourceType, TradeRiskCacularResultSourceType.over);
        //过滤手动维护的
        queryWrapper.eq(TradeRiskInfo::getHandmade,Boolean.TRUE);
        //风险日期
        queryWrapper.eq(riskInfoPvDTO.getRiskDate() != null, TradeRiskInfo::getRiskDate, riskInfoPvDTO.getRiskDate());
        //交易编号
        queryWrapper.eq(StringUtils.isNotBlank(riskInfoPvDTO.getTradeCode()),TradeRiskInfo::getTradeCode,riskInfoPvDTO.getTradeCode());
        Page<TradeRiskInfo> page = new Page<>(riskInfoPvDTO.getPageNo(), riskInfoPvDTO.getPageSize());
        page = this.page(page, queryWrapper);
        return page.convert(item -> TradeRiskPVInfoVO.builder()
                .tradeCode(item.getTradeCode())
                .riskDate(item.getRiskDate())
                .availableAmount(item.getAvailableAmount())
                .margin(item.getMargin())
                .delta(item.getDelta())
                .gamma(item.getGamma())
                .vega(item.getVega())
                .theta(item.getTheta())
                .rho(item.getRho())
                .build());
    }

    /**
     * 保存手动维护的风险信息
     * @param tradeRiskInfo 风险信息
     */
    private void handmadeTradeRiskInfo(TradeRiskInfo tradeRiskInfo) {
        LambdaQueryWrapper<TradeRiskInfo> tradeRiskInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeRiskInfoLambdaQueryWrapper.eq(TradeRiskInfo::getId, tradeRiskInfo.getId());
        tradeRiskInfoLambdaQueryWrapper.eq(TradeRiskInfo::getRiskDate, tradeRiskInfo.getRiskDate());
        if (Objects.isNull(tradeRiskInfoMapper.selectOne(tradeRiskInfoLambdaQueryWrapper))) {
            tradeRiskInfoMapper.insert(tradeRiskInfo);
        } else {
            tradeRiskInfoMapper.update(tradeRiskInfo, tradeRiskInfoLambdaQueryWrapper);
        }
    }
}
