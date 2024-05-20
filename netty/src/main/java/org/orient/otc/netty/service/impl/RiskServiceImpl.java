package org.orient.otc.netty.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.feign.VarietyClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.netty.dto.DeltaAdjustmentDto;
import org.orient.otc.api.netty.vo.RiskInfoVo;
import org.orient.otc.api.quote.dto.risk.TradeRiskCacularResult;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.netty.dto.RiskInfoQueryByPageDto;
import org.orient.otc.netty.dto.RiskInfoQueryDto;
import org.orient.otc.netty.dto.RiskTimeEditDTO;
import org.orient.otc.netty.service.RiskService;
import org.orient.otc.netty.vo.RiskTotalVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dzrh
 */
@Service
@Slf4j
public class RiskServiceImpl implements RiskService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UnderlyingManagerClient underlyingManagerClient;

    @Resource
    private ClientClient clientClient;

    @Resource
    private VarietyClient varietyClient;

    @Resource
    private  SystemConfigUtil systemConfigUtil;

    @Resource
    private MarketClient marketClient;

    /**
     *
     */
    @Override
    public RiskTotalVo getRiskInfoList(RiskInfoQueryDto dto) {
        LocalDate tradeDay = systemConfigUtil.getTradeDay();
        List<TradeRiskCacularResult> filter = this.getTradeRiskCacularResult(dto);
        RiskTotalVo riskTotalVo = new RiskTotalVo();
        if (dto.getSelectedUnderlyingCodeList() != null) {
            if (dto.getSelectedUnderlyingCodeList().contains("ALL")) {

                riskTotalVo.setDetailList(changDetailData(filter,tradeDay));
            } else {
                riskTotalVo.setDetailList(changDetailData(filter.stream()
                        //合约筛选
                        .filter(a -> dto.getSelectedUnderlyingCodeList().contains(a.getUnderlyingCode()))
                        .collect(Collectors.toList()),tradeDay));
            }
        }

        List<Object> values = stringRedisTemplate.opsForHash().values(RedisAdapter.TOTAL_PNL_BY_CLOSED);
        List<TradeRiskCacularResult> closedList = JSONArray.parseArray(values.toString(), TradeRiskCacularResult.class);
        //移除掉存续部分的已平仓数据
        List<String> idList = filter.stream().map(TradeRiskCacularResult::getId).collect(Collectors.toList());
        closedList.removeIf(a->idList.contains(a.getId()));
        riskTotalVo.setRiskInfoVoList(convertData(filter, filter(closedList, dto),tradeDay));


        String riskTimeStr = stringRedisTemplate.opsForValue().get(RedisAdapter.RISK_TIME);
        if (StringUtils.isNotBlank(riskTimeStr)) {
            riskTotalVo.setRiskTime(LocalDateTime.ofEpochSecond(Long.parseLong(riskTimeStr), 0, ZoneOffset.ofHours(8)));
        }
        return riskTotalVo;
    }
    private List<TradeRiskCacularResult> changDetailData(List<TradeRiskCacularResult> resultList, LocalDate tradeDay){
        for (TradeRiskCacularResult vo : resultList) {
            //雪球期权的敲出价格单独设置
            if (OptionTypeEnum.getSnowBall().contains(vo.getOptionType())) {
                if (vo.getKnockinBarrierRelative() != null && vo.getKnockinBarrierValue() != null && vo.getKnockinBarrierRelative()) {
                    vo.setKnockinBarrierValue(vo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(vo.getKnockinBarrierValue())));
                }
                if (vo.getStrikeOnceKnockedinRelative() != null && vo.getStrikeOnceKnockedinValue() != null && vo.getStrikeOnceKnockedinRelative()) {
                    vo.setStrikeOnceKnockedinValue(vo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(vo.getStrikeOnceKnockedinValue())));
                }
                if (vo.getStrike2OnceKnockedinRelative() != null && vo.getStrike2OnceKnockedinValue() != null && vo.getStrike2OnceKnockedinRelative()) {
                    vo.setStrike2OnceKnockedinValue(vo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(vo.getStrike2OnceKnockedinValue())));
                }
                //敲出价格单独设置
                TradeObsDateVO tradeObsDateVO = vo.getObsDateList().stream().filter(
                        a -> !a.getObsDate().isBefore(tradeDay)).findFirst().orElse(new TradeObsDateVO());
                if (tradeObsDateVO.getBarrierRelative() != null && tradeObsDateVO.getBarrierRelative()) {
                    vo.setBarrier(vo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeObsDateVO.getBarrier())));
                } else {
                    vo.setBarrier(tradeObsDateVO.getBarrier());
                }
            }
            //设置敲出标识
            if (OptionTypeEnum.getCallKnockOut().contains(vo.getOptionType())){
                //看涨期权最新价格大于或者等于敲出价格则为敲出
                if (vo.getObsDateList().stream().anyMatch(obs->obs.getObsDate().equals(tradeDay))){
                    vo.setIsKnockOut(vo.getLastPrice().compareTo(vo.getBarrier())>=0);
                }
            }
            if (OptionTypeEnum.getPutKnockOut().contains(vo.getOptionType())){
                //看跌期权最新价格小于或者等于敲出价格则为敲出
                if (vo.getObsDateList().stream().anyMatch(obs->obs.getObsDate().equals(tradeDay))){
                    vo.setIsKnockOut(vo.getLastPrice().compareTo(vo.getBarrier())<=0);
                }
            }
        }
        return  resultList;
    }
    private List<RiskInfoVo> convertData(List<TradeRiskCacularResult> list, List<TradeRiskCacularResult> closedList,LocalDate tradeDay) {
        //list= list.stream().filter(a -> a.getStatus()== SuccessStatusEnum.success).collect(Collectors.toList());
        //计算DeltaLots
        Map<String, BigDecimal> deltaLotsMap = list.stream().filter(risk -> risk.getDeltaLots() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getDeltaLots, BigDecimal::add)));
        //计算getDeltaCash
        Map<String, BigDecimal> deltaCashMap = list.stream().filter(risk -> risk.getDeltaCash() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getDeltaCash, BigDecimal::add)));
        //计算getGammaLots
        Map<String, BigDecimal> gammaLotsMap = list.stream().filter(risk -> risk.getGammaLots() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getGammaLots, BigDecimal::add)));
        //计算getGammaCash
        Map<String, BigDecimal> gammaCashMap = list.stream().filter(risk -> risk.getGammaCash() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getGammaCash, BigDecimal::add)));
        //计算getTheta
        Map<String, BigDecimal> thetaMap = list.stream().filter(risk -> risk.getTheta() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getTheta, BigDecimal::add)));
        //计算getVega
        Map<String, BigDecimal> vegaMap = list.stream().filter(risk -> risk.getVega() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getVega, BigDecimal::add)));
        //计算day1PnlMap
        Map<String, BigDecimal> day1PnlMap = list.stream().filter(risk -> risk.getDay1PnL() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getDay1PnL, BigDecimal::add)));
        //todayPnl
        Map<String, BigDecimal> todayPnlMap = list.stream().filter(risk -> risk.getTodayProfitLoss() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getTodayProfitLoss, BigDecimal::add)));
        //初始化的累计盈亏
        Map<Object, Object> initTotalPnlEntries = stringRedisTemplate.opsForHash().entries(RedisAdapter.INIT_TOTAL_PNL);
        Map<String, BigDecimal> initTotalPnltMap = initTotalPnlEntries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> new BigDecimal(e.getValue().toString())));
        //更新closedListTotalPnl
        closedList.stream().filter(risk -> risk.getTotalProfitLoss() != null)
                .forEach(item->item.setTotalProfitLoss(item.getTotalProfitLoss()
                        .subtract(initTotalPnltMap.getOrDefault(item.getId(),BigDecimal.ZERO))));
        //已平仓的合约累计盈亏
        Map<String, BigDecimal> totalPnlByClosedMap = closedList.stream()
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getTotalProfitLoss, BigDecimal::add)));
        //更新totalPnl
        list.stream().filter(risk -> risk.getTotalProfitLoss() != null)
                .forEach(item->item.setTotalProfitLoss(item.getTotalProfitLoss()
                        .subtract(initTotalPnltMap.getOrDefault(item.getId(),BigDecimal.ZERO))));
        //未平仓的合约累计盈亏
        Map<String, BigDecimal> totalPnlByNotClosedMap = list.stream().filter(risk -> risk.getTotalProfitLoss() != null)
                .collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode,
                        Collectors.reducing(BigDecimal.ZERO, TradeRiskCacularResult::getTotalProfitLoss, BigDecimal::add)));
        //合约最新价
        Map<String, BigDecimal> lastPriceMap =  list.stream().collect(
                Collectors.toMap(TradeRiskCacularResult::getUnderlyingCode, TradeRiskCacularResult::getLastPrice
                        ,(v1, v2) -> v2));
        //合约昨日收盘价
        Map<String, BigDecimal> lastDayTotalMarketMap = marketClient.getCloseMarketDataByDate(systemConfigUtil.getLastTradeDay());

        //指定的合约价格
        Map<Object, Object> riskMarkEntries = stringRedisTemplate.opsForHash().entries(RedisAdapter.RISK_MARK+tradeDay);
        Map<String, BigDecimal> riskMarkMap = riskMarkEntries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> new BigDecimal(e.getValue().toString())));
        //DELTA_ADJUSTMENT
        Map<Object, Object> deltaAdjustmentEntries = stringRedisTemplate.opsForHash().entries(RedisAdapter.DELTA_ADJUSTMENT);
        Map<String, BigDecimal> deltaAdjustmentMap = deltaAdjustmentEntries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> new BigDecimal(e.getValue().toString())));
        //合约代码
        Map<String,String> varietyCodeMap= list.stream().collect(Collectors.toMap(TradeRiskCacularResult::getUnderlyingCode
                ,TradeRiskCacularResult::getVarietyCode,(v1,v2)->v2));
        //整合处理数据
        List<RiskInfoVo> riskInfoVoList = new ArrayList<>();

        List<String> underlyingCodeList = list.stream().map(TradeRiskCacularResult::getUnderlyingCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        for (String underlyingCode : underlyingCodeList) {
            RiskInfoVo riskInfoVo = new RiskInfoVo();
            riskInfoVo.setUnderlyingCode(underlyingCode);
            riskInfoVo.setVarietyCode(varietyCodeMap.get(underlyingCode));
            //deltaAdjustment
            riskInfoVo.setDeltaAdjustment(deltaAdjustmentMap.getOrDefault(underlyingCode,BigDecimal.ZERO));

            /*
             * 求和汇总部分数据
             * */
            riskInfoVo.setDeltaLots(deltaLotsMap.get(underlyingCode).add(riskInfoVo.getDeltaAdjustment()));
            riskInfoVo.setDeltaCash(deltaCashMap.get(underlyingCode));
            riskInfoVo.setGammaLots(gammaLotsMap.get(underlyingCode));
            riskInfoVo.setGammaCash(gammaCashMap.get(underlyingCode));
            riskInfoVo.setTheta(thetaMap.get(underlyingCode));
            riskInfoVo.setVega(vegaMap.get(underlyingCode));
            riskInfoVo.setDay1PnL(day1PnlMap.get(underlyingCode));
            BigDecimal lastPrice =lastPriceMap.get(underlyingCode);
            riskInfoVo.setDeltaRl(riskInfoVo.getDeltaCash());
            //指定的合约价格
            riskInfoVo.setEditPrice(riskMarkMap.getOrDefault(underlyingCode, BigDecimal.ZERO));
            /*
             *平衡变动公式 标的价格*√(-Theta的和/(50*Gamma(1%Cash)的和))
             * 只有gamma大于0时才计算平衡变动
             */
            if (riskInfoVo.getGammaCash() != null && BigDecimal.ZERO.compareTo(riskInfoVo.getGammaCash()) != 0) {
                BigDecimal tempBigDecimal = riskInfoVo.getTheta().divide(riskInfoVo.getGammaCash().multiply(new BigDecimal("50")), 16, RoundingMode.HALF_UP);
                if (tempBigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                    riskInfoVo.setBalancedChanges(lastPrice.multiply(sqrt(tempBigDecimal.negate(), 16)).setScale(2,RoundingMode.HALF_UP));
                }
            }

            riskInfoVo.setLastPrice(lastPrice);
            if (lastDayTotalMarketMap.get(underlyingCode) != null && BigDecimal.ZERO.compareTo(lastPrice) < 0) {
                riskInfoVo.setChg(lastPrice.subtract(lastDayTotalMarketMap.get(underlyingCode)));
                //涨跌百分比=(最新-昨收)/昨收
                riskInfoVo.setChgPercent(lastPrice.subtract(lastDayTotalMarketMap.get(underlyingCode)).divide(lastDayTotalMarketMap.get(underlyingCode), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP) + "%");
            }
            BigDecimal totalPnlByClosed = totalPnlByClosedMap.getOrDefault(underlyingCode,BigDecimal.ZERO);
            riskInfoVo.setTotalPnlByClose(totalPnlByClosed);
            riskInfoVo.setTodayPnL(todayPnlMap.get(underlyingCode));
            //已平仓+未平仓的累计盈亏
            riskInfoVo.setTotalPnl(totalPnlByClosed.add(totalPnlByNotClosedMap.getOrDefault(underlyingCode,BigDecimal.ZERO)));
            riskInfoVoList.add(riskInfoVo);
        }

        return riskInfoVoList;
    }


    /**
     * 开根号
     * @param value 开根号值
     * @param scale 保留小数位
     * @return 结果
     */
    public static BigDecimal sqrt(BigDecimal value, int scale) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal num2 = BigDecimal.valueOf(2);
        int precision = 100;
        MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);
        BigDecimal deviation = value;
        int cnt = 0;
        while (cnt < precision) {
            deviation = (deviation.add(value.divide(deviation, mc))).divide(num2, mc);
            cnt++;
        }
        deviation = deviation.setScale(scale, RoundingMode.HALF_UP);
        return deviation;
    }

    /**
     * 修改搜索条件
     * @param value 搜索条件
     */
    @Override
    public void modifySearchCriteria(Channel channel, RiskInfoQueryDto value) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(
                this.getRiskInfoList(value))));
    }

    /**
     *
     */
    @Override
    public List<TradeRiskCacularResult> getTradeRiskCacularResult(RiskInfoQueryDto dto) {
        List<TradeRiskCacularResult> resultList = getTradeRiskCacularList();
        return resultList.stream()
                //过滤存续数量为0的数据
                .filter(item -> {
                    if (dto.getIsNotClose() != null && dto.getIsNotClose()) {
                        return item.getAvailableVolume().compareTo(BigDecimal.ZERO)>0;
                    } else {
                        return true;
                    }
                })
                //簿记账户筛选
                .filter(item -> {
                    if (dto.getAssetIdList() != null && !dto.getAssetIdList().isEmpty()) {
                        return dto.getAssetIdList().contains(item.getAssetId());
                    } else {
                        return true;
                    }
                })
                //客户筛选
                .filter(item -> {
                    if (dto.getClientIdList() != null && !dto.getClientIdList().isEmpty()) {
                        return dto.getClientIdList().contains(item.getClientId());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //标的合约 不为空
                    if (!CollectionUtils.isEmpty(dto.getUnderlyingCodeList())) {
                        return dto.getUnderlyingCodeList().contains(item.getUnderlyingCode());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //标的品种筛选 不为空
                    if (!CollectionUtils.isEmpty(dto.getVarietyCodeList())) {
                        return dto.getVarietyCodeList().contains(item.getVarietyCode());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //产业链筛选
                    if (!CollectionUtils.isEmpty(dto.getVarietyTypeList())){
                        return dto.getVarietyTypeList().contains(item.getVarietyCode());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //交易员筛选
                    if (!CollectionUtils.isEmpty(dto.getTraderList())){
                        return dto.getTraderList().contains(item.getVarietyCode());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //客户端过滤自定义期权
                    AuthorizeInfo authorizeInfo= ThreadContext.getAuthorizeInfo();
                    if (authorizeInfo!=null && authorizeInfo.getLoginForm()==0){
                        return !OptionTypeEnum.AICustomPricer.equals(item.getOptionType());
                    }else {
                        return  true;
                    }
                })
                //交易编号筛选
                .filter(item -> {
                    if (dto.getTradeCode() != null && !dto.getTradeCode().isEmpty()) {
                        return item.getTradeCode() != null && item.getTradeCode().contains(dto.getTradeCode());
                    } else {
                        return true;
                    }
                })
                //交易类型筛选
                .filter(item -> {
                    if (dto.getTradeTypeList() != null && !dto.getTradeTypeList().isEmpty()) {
                        if (item.getOptionType() != null) {
                            return dto.getTradeTypeList().contains(item.getOptionType().name());
                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }
                })
                //看涨看跌
                .filter(item -> {
                    if (dto.getCallOrPut() != null) {
                        return dto.getCallOrPut().equals(item.getCallOrPut()) || (dto.getCallOrPut() == CallOrPutEnum.call ?
                                OptionTypeEnum.getCallOptionType().contains(item.getOptionType()) :
                                OptionTypeEnum.getPutOptionType().contains(item.getOptionType()));
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    private List<TradeRiskCacularResult> filter(List<TradeRiskCacularResult> closedList, RiskInfoQueryDto dto) {
        return closedList.stream()
                //簿记账户筛选
                .filter(item -> {
                    if (dto.getAssetIdList() != null && !dto.getAssetIdList().isEmpty()) {
                        return dto.getAssetIdList().contains(item.getAssetId());
                    } else {
                        return true;
                    }
                })
                //客户筛选
                .filter(item -> {
                    if (dto.getClientIdList() != null && !dto.getClientIdList().isEmpty()) {
                        return dto.getClientIdList().contains(item.getClientId());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //标的合约 不为空
                    if (!CollectionUtils.isEmpty(dto.getUnderlyingCodeList())) {
                        return dto.getUnderlyingCodeList().contains(item.getUnderlyingCode());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //标的品种筛选 不为空
                    if (!CollectionUtils.isEmpty(dto.getVarietyCodeList())) {
                        return dto.getVarietyCodeList().contains(item.getVarietyCode());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //产业链筛选
                    if (!CollectionUtils.isEmpty(dto.getVarietyTypeList())){
                        return dto.getVarietyTypeList().contains(item.getVarietyCode());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //交易员筛选
                    if (!CollectionUtils.isEmpty(dto.getTraderList())){
                        return dto.getTraderList().contains(item.getVarietyCode());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //客户端过滤自定义期权
                    AuthorizeInfo authorizeInfo= ThreadContext.getAuthorizeInfo();
                    if (authorizeInfo!=null && authorizeInfo.getLoginForm()==0){
                        return !OptionTypeEnum.AICustomPricer.equals(item.getOptionType());
                    }else {
                        return  true;
                    }
                })
                //交易编号筛选
                .filter(item -> {
                    if (dto.getTradeCode() != null && !dto.getTradeCode().isEmpty()) {
                        return item.getTradeCode() != null && item.getTradeCode().contains(dto.getTradeCode());
                    } else {
                        return true;
                    }
                })
                //交易类型筛选
                .filter(item -> {
                    if (dto.getTradeTypeList() != null && !dto.getTradeTypeList().isEmpty()) {
                        if (item.getOptionType() != null) {
                            return dto.getTradeTypeList().contains(item.getOptionType().name());
                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void sendTradeRiskCacularResult(Channel channel, RiskInfoQueryDto value) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(
                this.getTradeRiskCacularResult(value))));
    }

    @Override
    public Set<String> getVarietyList() {
        List<TradeRiskCacularResult> riskList = getTradeRiskCacularList();
        return riskList.stream().map(TradeRiskCacularResult::getVarietyCode).collect(Collectors.toSet());
    }

    @Override
    public List<ClientVO> getClientList() {
        List<ClientVO> clientVOList = clientClient.getClientListByIds(new HashSet<>());
        List<TradeRiskCacularResult> riskList = getTradeRiskCacularList();
        List<Integer> clientIdList = riskList.stream().map(TradeRiskCacularResult::getClientId).distinct().collect(Collectors.toList());
        return clientVOList.stream().filter(a -> {
            for (Integer clientId : clientIdList) {
                if (a.getId().equals(clientId)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    @Override
    public void editDeltaAdjustment(DeltaAdjustmentDto dto) {
        if (dto.getAdjustment() != null && dto.getUnderlyingCode() != null) {
            stringRedisTemplate.opsForHash().put(RedisAdapter.DELTA_ADJUSTMENT, dto.getUnderlyingCode(), dto.getAdjustment().toString());
        }
    }

    @Override
    public void editRiskTime(RiskTimeEditDTO riskTimeEditDto) {
        if (riskTimeEditDto.getRiskTime() != null) {
            stringRedisTemplate.opsForValue().set(RedisAdapter.RISK_TIME
                    , String.valueOf(riskTimeEditDto.getRiskTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond()));
        }
    }

    /**
     * 从Redis获取计算结果
     * @return 计算结果
     */
    private List<TradeRiskCacularResult> getTradeRiskCacularList() {
        List<Object> otcList = stringRedisTemplate.opsForHash().values(RedisAdapter.TRADE_RISK_RESULT);
        return JSONObject.parseArray(otcList.toString(), TradeRiskCacularResult.class);
    }

    @Override
    public Page<TradeRiskCacularResult> getTradeListByPage(RiskInfoQueryByPageDto dto) {
        RiskInfoQueryDto riskInfoQueryDto = new RiskInfoQueryDto();
        BeanUtils.copyProperties(dto, riskInfoQueryDto);
        riskInfoQueryDto.setIsNotClose(true);
        List<TradeRiskCacularResult> dataList = getTradeRiskCacularResult(riskInfoQueryDto);
        //标的品种筛选
        if (!CollectionUtils.isEmpty(dto.getVarietyCodeList())) {
            Set<String> underlyingCodeSet;
            Map<String, Integer> map = varietyClient.getVarietyMap();
            List<Integer> varietyIdSet = new ArrayList<>();
            for (String varietyCode : dto.getVarietyCodeList()) {
                if (map.containsKey(varietyCode)) {
                    Integer varietyId = map.get(varietyCode);
                    varietyIdSet.add(varietyId);
                }
            }
            if (!CollectionUtils.isEmpty(varietyIdSet)) {
                // 获取标的品种对应的合约信息
                List<UnderlyingManagerVO> underlyingManagerlist = underlyingManagerClient.getUnderlyingListByVarietyIds(varietyIdSet);
                underlyingCodeSet = underlyingManagerlist.stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toSet());
            } else {
                underlyingCodeSet = new HashSet<>();
            }
            Set<String> finalUnderlyingCodeSet = underlyingCodeSet;
            dataList = dataList.stream().filter(//标的品种筛选
                    item -> finalUnderlyingCodeSet.contains(item.getUnderlyingCode())
            ).collect(Collectors.toList());
        }
        int pageSize;
        int pageNo;
        if (dto.getPageNo() == null || dto.getPageSize() == null) {
            pageNo = 1;
            pageSize = 100;
        } else {
            pageNo = dto.getPageNo();
            pageSize = dto.getPageSize();
        }
        int totalCount = dataList.size(); // 总条数
        int totalPage; // 总页数
        totalPage = totalCount / pageSize;
        if (totalCount % pageSize != 0) {
            ++totalPage;
        }
        List<TradeRiskCacularResult> list = dataList.stream().skip((long) (pageNo - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        Page<TradeRiskCacularResult> page = new Page<>();
        page.setRecords(list);
        page.setTotal(totalCount);
        page.setSize(pageSize);
        page.setCurrent(pageNo);
        page.setPages(totalPage);
        return page;
    }
}
