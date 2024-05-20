package org.orient.otc.quote.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.dm.feign.InstrumentClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.market.dto.CloseDatePriceByDateDto;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.SuccessStatusEnum;
import org.orient.otc.api.quote.enums.TradeRiskCacularResultType;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.api.user.dto.ExchangeAccountQueryDto;
import org.orient.otc.api.user.feign.AssetUnitClient;
import org.orient.otc.api.user.feign.ExchangeAccountClient;
import org.orient.otc.api.user.vo.AssetunitGroupVo;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.TradeRiskInfoDto;
import org.orient.otc.quote.dto.risk.ExchangeRealTimePos;
import org.orient.otc.quote.dto.risk.PositionPageListDto;
import org.orient.otc.quote.entity.*;
import org.orient.otc.quote.enums.ExchangeEodType;
import org.orient.otc.quote.enums.OpenOrCloseEnum;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.*;
import org.orient.otc.quote.service.ExchangePositionService;
import org.orient.otc.quote.service.RiskService;
import org.orient.otc.quote.util.HutoolUtil;
import org.orient.otc.quote.vo.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RiskServiceImpl implements RiskService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private InstrumentClient instrumentClient;


    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ExchangeAccountClient exchangeAccountClient;


    @Resource
    private ExchangeTradeMapper exchangeTradeMapper;

    @Resource
    private ExchangePositionService exchangePositionService;

    @Resource
    private ExchangePositionMapper exchangePositionMapper;

    @Resource
    private ExchangePositionCheckMapper exchangePositionCheckMapper;

    @Resource
    private ExchangeTradeTmpMapper exchangeTradeTmpMapper;
    @Resource
    private ExchangePositionTmpMapper exchangePositionTmpMapper;
    @Resource
    private TradeMngMapper tradeMngMapper;

    @Resource
    private TradeCloseMngMapper tradeCloseMngMapper;
    @Resource
    private TradeRiskInfoMapper tradeRiskInfoMapper;
    @Resource
    ClientClient client;
    @Resource
    AssetUnitClient assetUnitClient;

    @Resource
    UnderlyingManagerClient underlyingManagerClient;

    @Resource
    MarketClient marketClient;


    @Override
    public Boolean reCalculationPos() {
        RLock lock = redissonClient.getFairLock("exchangeCalcPos");//重头计算的时候，不能再接受数据进行计算了
        lock.lock();
        try {
            //取当前交易日和前一个交易日
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisAdapter.SYSTEM_CONFIG_INFO);
            Map<String, String> systemInfoMap = entries.entrySet().stream().collect(
                    Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
            String curTrdDay = LocalDate.parse(systemInfoMap.get(SystemConfigEnum.tradeDay.name())).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String preTrdDay = LocalDate.parse(systemInfoMap.get(SystemConfigEnum.lastTradeDay.name())).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            //取当前交易日已经入库的成交记录
            LambdaQueryWrapper<ExchangeTrade> qryTrdWrapper = new LambdaQueryWrapper<>();
            qryTrdWrapper.eq(ExchangeTrade::getTradingDay, curTrdDay);
            qryTrdWrapper.eq(ExchangeTrade::getIsDeleted, 0);
            List<ExchangeTrade> tradeData = exchangeTradeMapper.selectList(qryTrdWrapper);
            //重新初始化持仓数据
            this.initPosData(preTrdDay);

            //重新计算当日的持仓信息
            for (ExchangeTrade tradeDatum : tradeData) {
                calcPos(tradeDatum);
            }
        } catch (Exception e) {
            log.error("重新计算场内异常：", e);
        } finally {
            lock.unlock();
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean initPosData(String preTrdDay) {
        //取上一个交易日的持仓
        List<ExchangeRealTimePos> preTrdDayPosData = exchangePositionService.selectPositionByTradingDay(preTrdDay);
        //清除redis中缓存的持仓
        Set<String> curTrdDayKeySet = stringRedisTemplate.keys(RedisAdapter.EXCHANGE_POSITION_INFO + "*");
        if (curTrdDayKeySet != null) {
            stringRedisTemplate.delete(curTrdDayKeySet);
        }
        //初始化redis中持仓数据
        for (ExchangeRealTimePos pos : preTrdDayPosData) {
            String redisPosKey = RedisAdapter.EXCHANGE_POSITION_INFO + pos.getInvestorID();
            String redisPosDataKey = pos.getInstrumentID() + "_" + pos.getPosiDirection();
            //持仓为0的不用初始化
            if (pos.getPosition() == 0) {
                continue;
            }
            pos.setOpenAmount(0.0);
            pos.setOpenVolume(0);
            pos.setCloseAmount(0.0);
            pos.setCloseVolume(0);
            pos.setDay1PnL(BigDecimal.ZERO);
            pos.setTotalPnl(0.0);
            if (pos.getTradeCost() == null) {
                pos.setTradeCost(BigDecimal.ZERO);
            }
            stringRedisTemplate.opsForHash().put(redisPosKey, redisPosDataKey, JSONObject.toJSONString(pos));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean copyPosDataToNextTradeDay() {
        Set<String> tradeDayKeySet = stringRedisTemplate.keys(RedisAdapter.EXCHANGE_POSITION_INFO + "*");
        //初始化redis中持仓数据
        if (tradeDayKeySet != null) {
            for (String tradeDayKey : tradeDayKeySet) {
                Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(tradeDayKey);
                Map<String, ExchangeRealTimePos> posDataMap = entries.entrySet().stream().collect(
                        Collectors.toMap(e -> String.valueOf(e.getKey())
                                , e -> JSONObject.parseObject(e.getValue().toString(), ExchangeRealTimePos.class)));
                for (Map.Entry<String, ExchangeRealTimePos> entry : posDataMap.entrySet()) {
                    ExchangeRealTimePos pos = entry.getValue();
                    //如果持仓数量为0则删除持仓信息
                    if (pos.getPosition() == 0) {
                        stringRedisTemplate.opsForHash().delete(tradeDayKey, entry.getKey());
                        continue;
                    }
                    pos.setYdPosition(pos.getPosition());
                    pos.setOpenAmount(0.0);
                    pos.setOpenVolume(0);
                    pos.setCloseAmount(0.0);
                    pos.setCloseVolume(0);
                    pos.setDay1PnL(BigDecimal.ZERO);
                    stringRedisTemplate.opsForHash().put(tradeDayKey, entry.getKey(), JSONObject.toJSONString(pos));
                }
            }
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public SettlementVO updatePosData(String tradeDay) {
        SettlementVO settlementVo = new SettlementVO();
        settlementVo.setIsSuccess(Boolean.TRUE);
        //控制仅允许调整一周以内的场内补单记录
       LocalDate lastWeekDay = LocalDate.now().minusWeeks(1);
       LocalDate supplementaryDay= LocalDate.parse(tradeDay, DatePattern.PURE_DATE_FORMATTER);
        BussinessException.E_300503.assertTrue(lastWeekDay.isBefore(supplementaryDay));
        StringBuilder msg = new StringBuilder();
        //获取今日补单的交易记录
        LambdaQueryWrapper<ExchangeTrade> tradeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeLambdaQueryWrapper.eq(ExchangeTrade::getTradingDay, tradeDay);
        tradeLambdaQueryWrapper.like(ExchangeTrade::getOrderSysID, "\\_");
        tradeLambdaQueryWrapper.eq(ExchangeTrade::getIsDeleted, 0);
        List<ExchangeTrade> tradeList = exchangeTradeMapper.selectList(tradeLambdaQueryWrapper);
        if (!tradeList.isEmpty()) {
            for (ExchangeTrade trade : tradeList) {
                msg.append(calculatePosition(trade)).append("\r\n");
            }
        } else {
            msg.append("今日无补单交易");
        }
        settlementVo.setMsg(msg.toString());
        return settlementVo;
    }

    /**
     * 根据交易记录更正持仓信息
     * @param trade 交易记录
     */
    private StringBuilder calculatePosition(ExchangeTrade trade) {
        StringBuilder retMsg = new StringBuilder();
        retMsg.append("处理补单信息:").append(trade.getOrderSysID());
        String redisTradeKey = RedisAdapter.SUPPLEMENTARY_ORDER + trade.getTradeID() + "_" + trade.getDirection() + "_" + trade.getExchangeID() + "_" + trade.getTradingDay();
        //只有比对持仓不一致时才需要更新持仓信息
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisTradeKey))) {
            return retMsg.append("跳过;");
        }
        //先确定持仓方向
        String posiDirection = getPosDirection(trade.getOffsetFlag(), trade.getDirection());
        LambdaQueryWrapper<ExchangePosition> positionLambdaQueryWrapper = new LambdaQueryWrapper<ExchangePosition>()
                .eq(ExchangePosition::getInvestorID, trade.getInvestorID())
                .eq(ExchangePosition::getInstrumentID, trade.getInstrumentID())
                .eq(ExchangePosition::getPosiDirection, posiDirection)
                .eq(ExchangePosition::getTradingDay, trade.getTradingDay())
                .eq(ExchangePosition::getIsDeleted, 0);
        ExchangePosition position = exchangePositionMapper.selectOne(positionLambdaQueryWrapper);
        boolean isAdd = false;
        if (Objects.isNull(position)) {
            isAdd=true;
            position = new ExchangePosition();
            position.setInstrumentID(trade.getInstrumentID());
            position.setBrokerID(trade.getBrokerID());
            position.setInvestorID(trade.getInvestorID());
            position.setExchangeID(trade.getExchangeID());
            position.setPosiDirection(posiDirection);
            position.setTradingDay(trade.getTradingDay());
            position.setHedgeFlag("1");
            position.setYdPosition(0);
            position.setPosition(0);
            position.setOpenVolume(0);
            position.setCloseVolume(0);
            position.setOpenAmount(0.0);
            position.setCloseAmount(0.0);
            position.setPreSettlementPrice(0.0);
            position.setSettlementPrice(0.0);
            position.setCloseProfit(0.0);
            position.setPositionProfit(0.0);
            position.setUseMargin(0.0);
            position.setCommission(0.0);
            position.setOpenCost(0.0);

        }
        retMsg.append("持仓信息由->");
        retMsg.append("持仓数量:").append(position.getPosition());
        //更新持仓数量与成本
        if ("0".equals(trade.getOffsetFlag())) {
            position.setPosition(position.getPosition() + trade.getVolume());
            position.setOpenVolume(position.getOpenVolume() + trade.getVolume());
            position.setOpenAmount(position.getOpenAmount() + trade.getVolume() * trade.getPrice() * getMultiple(trade.getInstrumentID()));
        } else {
            position.setPosition(position.getPosition() - trade.getVolume());
            position.setCloseVolume(position.getCloseVolume() + trade.getVolume());
            position.setCloseAmount(position.getCloseAmount() + trade.getVolume() * trade.getPrice() * getMultiple(trade.getInstrumentID()));
        }
        retMsg.append("更新为->");
        retMsg.append("持仓数量:").append(position.getPosition());
        if (isAdd){
            exchangePositionMapper.insert(position);
        }else {
            exchangePositionMapper.update(position, positionLambdaQueryWrapper);
        }
        stringRedisTemplate.opsForValue().set(redisTradeKey, JSONObject.toJSONString(trade), 7, TimeUnit.DAYS);
        return retMsg;
    }

    private Integer getMultiple(String instID) {
        //查询期货合约乘数(微服务内部请求)
        InstrumentInfoVo instInfo = instrumentClient.getInstrumentInfo(instID);
        return instInfo.getVolumeMultiple();
    }

    @Override
    public String setTodayOpenAndClose() {
        LocalDate tradeDay = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
      String tradeDayStr=  tradeDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Map<OpenOrCloseEnum, Map<String, BigDecimal>> result=  this.getOpenAndClose(tradeDay);
        stringRedisTemplate.delete(RedisAdapter.TODAY_OPEN_TRADE_AMOUNT+tradeDayStr);
        for (Map.Entry<String, BigDecimal> entry : result.getOrDefault(OpenOrCloseEnum.open,new HashMap<>()).entrySet()) {
            stringRedisTemplate.opsForHash().put(RedisAdapter.TODAY_OPEN_TRADE_AMOUNT+tradeDayStr, entry.getKey(), entry.getValue().toString());
        }
        stringRedisTemplate.delete(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT+tradeDayStr);
        for (Map.Entry<String, BigDecimal> entry : result.getOrDefault(OpenOrCloseEnum.close,new HashMap<>()).entrySet()) {
            stringRedisTemplate.opsForHash().put(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT+tradeDayStr, entry.getKey(), entry.getValue().toString());
        }
        return "重置成功";
    }

    @Override
    public Map<OpenOrCloseEnum, Map<String, BigDecimal>> getOpenAndClose(LocalDate tradeDay) {
        String tradeDayStr=  tradeDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Map<OpenOrCloseEnum, Map<String, BigDecimal>> result= new HashMap<>();
        //场外数据开平方向需要转换为我们的方向
        LambdaQueryWrapper<TradeCloseMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeCloseMng::getCloseDate, tradeDay);
        queryWrapper.eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO);
        List<TradeCloseMng> tradeCloseMngList = tradeCloseMngMapper.selectList(queryWrapper);
        Map<String, BigDecimal> otcCloseAmountMap = tradeCloseMngList.stream().collect(Collectors.groupingBy(TradeCloseMng::getTradeCode,
                Collectors.reducing(BigDecimal.ZERO, e -> e.getCloseTotalAmount().negate(), BigDecimal::add)));

        List<TradeMng> tradeMngList = tradeMngMapper.selectList(new LambdaQueryWrapper<TradeMng>()
                .eq(TradeMng::getTradeDate, tradeDay)
                .eq(TradeMng::getIsDeleted, IsDeletedEnum.NO)
        );
        Map<String, BigDecimal> otcOpenAmountMap = tradeMngList.stream().collect(Collectors.toMap(TradeMng::getTradeCode, mng -> mng.getTotalAmount().negate()));

        List<ExchangeTrade> exchangeTradeList = exchangeTradeMapper.selectList(new LambdaQueryWrapper<ExchangeTrade>()
                .eq(ExchangeTrade::getIsDeleted, IsDeletedEnum.NO)
                .eq(ExchangeTrade::getTradingDay, tradeDayStr));
        Set<String> instrumentIDSet = exchangeTradeList.stream().map(ExchangeTrade::getInstrumentID).collect(Collectors.toSet());
        List<InstrumentInfoVo> instrumentInfoVoList = instrumentClient.getInstrumentInfoByIds(instrumentIDSet);
        Map<String, Integer> volumeMultipleMap = instrumentInfoVoList.stream().collect(Collectors.toMap(InstrumentInfoVo::getInstrumentId, InstrumentInfoVo::getVolumeMultiple));

        //平仓数据处理
        Map<String, BigDecimal> exchangeCloseAmountMap = exchangeTradeList.stream().filter(e -> !"0".equals(e.getOffsetFlag())).collect(
                Collectors.groupingBy(exchangeTrade -> exchangeTrade.getInvestorID() + "_" + exchangeTrade.getInstrumentID() + "_"
                                + ("1".equals(exchangeTrade.getDirection()) ? ExchangeEodType.LONG.name() : ExchangeEodType.SHORT.name()),
                        Collectors.reducing(BigDecimal.ZERO, e -> BigDecimal.valueOf(e.getPrice() * e.getVolume() * volumeMultipleMap.get(e.getInstrumentID())), BigDecimal::add)));
        //开仓数据处理
        Map<String, BigDecimal> exchangeOpenAmountMap = exchangeTradeList.stream().filter(e -> "0".equals(e.getOffsetFlag())).collect(
                Collectors.groupingBy(exchangeTrade -> exchangeTrade.getInvestorID() + "_" + exchangeTrade.getInstrumentID() + "_"
                                + ("0".equals(exchangeTrade.getDirection()) ? ExchangeEodType.LONG.name() : ExchangeEodType.SHORT.name()),
                        Collectors.reducing(BigDecimal.ZERO, e -> BigDecimal.valueOf(e.getPrice() * e.getVolume() * volumeMultipleMap.get(e.getInstrumentID())), BigDecimal::add)));
        otcOpenAmountMap.putAll(exchangeOpenAmountMap);
        result.put(OpenOrCloseEnum.open,otcOpenAmountMap);
        otcCloseAmountMap.putAll(exchangeCloseAmountMap);
        result.put(OpenOrCloseEnum.close,otcCloseAmountMap);
        return result;
    }

    @Override
    public void calcPos(ExchangeTrade tradeData) {
        String today = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //先确定持仓方向
        String posDirection = getPosDirection(tradeData.getOffsetFlag(), tradeData.getDirection());
        //查询期货合约信息(微服务内部请求)，设置合约的基本信息
        InstrumentInfoVo instInfo = instrumentClient.getInstrumentInfo(tradeData.getInstrumentID());
        String redisPosDataKey = tradeData.getInstrumentID() + "_" + posDirection;
        String redisPosKey = RedisAdapter.EXCHANGE_POSITION_INFO + tradeData.getInvestorID();
        ExchangeRealTimePos position;
        if (stringRedisTemplate.opsForHash().hasKey(redisPosKey, redisPosDataKey)) {
            position = JSONObject.parseObject(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(redisPosKey, redisPosDataKey)).toString(), ExchangeRealTimePos.class);
        } else {
            //查询期货合约信息(微服务内部请求)，设置合约的基本信息
            position = new ExchangeRealTimePos(tradeData.getBrokerID(), tradeData.getInvestorID(), tradeData.getExchangeID(), posDirection);
            position.setInstrumentID(tradeData.getInstrumentID());
            position.setExpireDate(instInfo.getExpireDate());
            position.setOptionsType(instInfo.getOptionsType());
            position.setStrikePrice(instInfo.getStrikePrice());
            position.setUnderlyingCode(instInfo.getOptionsType() == 0 ? instInfo.getInstrumentId().toUpperCase() : instInfo.getUnderlyingInstrId().toUpperCase());
            position.setDay1PnL(BigDecimal.ZERO);
        }
        //更新持仓数量与成本
        if ("0".equals(tradeData.getOffsetFlag())) {
            position.setPosition(position.getPosition() + tradeData.getVolume());
            position.setTradeCost(position.getTradeCost().add(BigDecimal.valueOf(tradeData.getVolume() * tradeData.getPrice() * instInfo.getVolumeMultiple())));
            position.setOpenVolume(position.getOpenVolume() + tradeData.getVolume());
            position.setOpenAmount(position.getOpenAmount() + tradeData.getVolume() * tradeData.getPrice() * instInfo.getVolumeMultiple());
        } else {
            position.setPosition(position.getPosition() - tradeData.getVolume());
            position.setTradeCost(position.getTradeCost().subtract(BigDecimal.valueOf(tradeData.getVolume() * tradeData.getPrice() * instInfo.getVolumeMultiple())));
            position.setCloseVolume(position.getCloseVolume() - tradeData.getVolume());
            position.setCloseAmount(position.getCloseAmount() - tradeData.getVolume() * tradeData.getPrice() * instInfo.getVolumeMultiple());
        }
        //redis缓存实时计算的持仓数据
        stringRedisTemplate.opsForHash().put(redisPosKey, redisPosDataKey, JSONObject.toJSONString(position));
    }


    @Override
    public void checkExchangePos() {
        List<ExchangeAccountFeignVO> list = exchangeAccountClient.getList();
        for (ExchangeAccountFeignVO exchangeAccountFeignVO : list) {
            this.checkExchangePosByAccount(exchangeAccountFeignVO.getAccount());
        }
    }

    @Override
    public void checkExchangePosByAccount(String exchangeAccount) {
        String today = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //数据库中的数据是场内返回正确的数据
        List<ExchangePositionTmp> exchangePositionTmpList = exchangePositionTmpMapper.selectList(new LambdaQueryWrapper<ExchangePositionTmp>().eq(ExchangePositionTmp::getInvestorID, exchangeAccount)
                .eq(ExchangePositionTmp::getTradingDay, today).eq(ExchangePositionTmp::getIsDeleted, 0));

        //删除之前校验的记录
        LambdaQueryWrapper<ExchangePositionCheck> lambdaQueryWrapper = new LambdaQueryWrapper<ExchangePositionCheck>().eq(ExchangePositionCheck::getInvestorId, exchangeAccount)
                .eq(ExchangePositionCheck::getTradingDay, today)
                .eq(ExchangePositionCheck::getIsDeleted, 0);
        ExchangePositionCheck ep = new ExchangePositionCheck();
        ep.setIsDeleted(1);
        exchangePositionCheckMapper.update(ep, lambdaQueryWrapper);
        //校验
        for (ExchangePositionTmp exchangePositionTmp : exchangePositionTmpList) {
            String redisPosDataKey = exchangePositionTmp.getInstrumentID() + "_" + exchangePositionTmp.getPosiDirection();
            //融行挂单未成交的不需要校验
            if (exchangePositionTmp.getPosition() == 0 && exchangePositionTmp.getCloseVolume() == 0 && exchangePositionTmp.getOpenVolume() == 0) {
                continue;
            }
            ExchangePositionCheck exchangePositionCheck = new ExchangePositionCheck();
            exchangePositionCheck.setInvestorId(exchangeAccount);
            exchangePositionCheck.setPosiDirection(Integer.valueOf(exchangePositionTmp.getPosiDirection()));
            exchangePositionCheck.setTradingDay(LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString()));
            exchangePositionCheck.setInstrumentId(exchangePositionTmp.getInstrumentID());
            exchangePositionCheck.setStatus(SuccessStatusEnum.faild);
            //redis中是我们自己计算的
            if (stringRedisTemplate.opsForHash().hasKey(RedisAdapter.EXCHANGE_POSITION_INFO + exchangeAccount, redisPosDataKey)) {

                ExchangeRealTimePos exchangeRealTimePos = JSONObject.parseObject(
                        Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.EXCHANGE_POSITION_INFO + exchangeAccount, redisPosDataKey)).toString(), ExchangeRealTimePos.class);

                if (exchangeRealTimePos.getPosition().equals(exchangePositionTmp.getPosition())) {
                    exchangePositionCheck.setStatus(SuccessStatusEnum.success);
                    exchangePositionCheck.setCheckMsg("Redis："+exchangeRealTimePos.getPosition()+",融行:"+exchangePositionTmp.getPosition());
                    exchangePositionTmpMapper.update(exchangePositionTmp, new LambdaQueryWrapper<ExchangePositionTmp>()
                            .eq(ExchangePositionTmp::getInstrumentID, exchangePositionTmp.getInstrumentID())
                            .eq(ExchangePositionTmp::getInvestorID, exchangePositionTmp.getInvestorID())
                            .eq(ExchangePositionTmp::getPosiDirection, exchangePositionTmp.getPosiDirection())
                            .eq(ExchangePositionTmp::getTradingDay, exchangePositionTmp.getTradingDay())
                            .eq(ExchangePositionTmp::getIsDeleted, 0));
                } else {
                    exchangePositionCheck.setCheckMsg("Redis："+exchangeRealTimePos.getPosition()+",融行:"+exchangePositionTmp.getPosition());
                    exchangePositionCheck.setStatus(SuccessStatusEnum.faild);
                }
            } else {
                exchangePositionCheck.setStatus(SuccessStatusEnum.faild);
                exchangePositionCheck.setCheckMsg("Redis：无持仓,融行:"+exchangePositionTmp.getPosition());
                exchangePositionCheck.setPosiDirection(Integer.valueOf(exchangePositionTmp.getPosiDirection()));
            }

            LambdaQueryWrapper<ExchangePositionCheck> eq = new LambdaQueryWrapper<ExchangePositionCheck>().eq(ExchangePositionCheck::getInvestorId, exchangeAccount)
                    .eq(ExchangePositionCheck::getTradingDay, today).eq(ExchangePositionCheck::getInstrumentId, exchangePositionTmp.getInstrumentID())
                    .eq(ExchangePositionCheck::getIsDeleted, 0);
            ExchangePositionCheck e = exchangePositionCheckMapper.selectOne(eq);
            if (Objects.isNull(e)) {
                exchangePositionCheckMapper.insert(exchangePositionCheck);
            } else {
                exchangePositionCheckMapper.update(exchangePositionCheck, eq);
            }
        }
    }

    /**
     * 获取持仓方向
     * @param offsetFlag 开平仓
     * @param direction  买卖方向
     * @return 多空方向
     */
    private String getPosDirection(String offsetFlag, String direction) {

        if ("0".equals(offsetFlag)) {
            if ("0".equals(direction)) {
                return "2";
            } else {
                return "3";
            }
        } else {
            if ("0".equals(direction)) {
                return "3";
            } else {
                return "2";
            }
        }
    }

    @Override
    public Boolean fromTmpToExchangeTrade() {
        List<ExchangeTradeTmp> exchangeTradeTmpList = exchangeTradeTmpMapper.selectList(new LambdaQueryWrapper<ExchangeTradeTmp>()
                .eq(ExchangeTradeTmp::getIsDeleted, IsDeletedEnum.NO));
        for (ExchangeTradeTmp exchangeTradeTmp : exchangeTradeTmpList) {
            LambdaQueryWrapper<ExchangeTrade> queryWrapper = new LambdaQueryWrapper<ExchangeTrade>()
                    .eq(ExchangeTrade::getTradeID, exchangeTradeTmp.getTradeID())
                    .eq(ExchangeTrade::getDirection, exchangeTradeTmp.getDirection())
                    .eq(ExchangeTrade::getExchangeID, exchangeTradeTmp.getExchangeID())
                    .eq(ExchangeTrade::getTradingDay, exchangeTradeTmp.getTradingDay());
            Long count = exchangeTradeMapper.selectCount(queryWrapper);
            ExchangeTrade exchangeTrade = JSONObject.parseObject(JSONObject.toJSONString(exchangeTradeTmp), ExchangeTrade.class);
            exchangeTrade.setUpdateTime(null);
            exchangeTrade.setCreateTime(null);
            if (count > 0) {
                exchangeTradeMapper.update(exchangeTrade, queryWrapper);
            } else {
                exchangeTradeMapper.insert(exchangeTrade);
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean fromTmpToExchangePosition() {
        String today = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ExchangePosition exchangePosition = new ExchangePosition();
        exchangePosition.setIsDeleted(1);
        exchangePositionMapper.update(exchangePosition, new LambdaQueryWrapper<ExchangePosition>()
                .eq(ExchangePosition::getTradingDay, today)
                .eq(ExchangePosition::getIsDeleted, 0));

        List<ExchangePositionTmp> exchangePositionTmpList = exchangePositionTmpMapper.selectList(new LambdaQueryWrapper<ExchangePositionTmp>()
                .eq(ExchangePositionTmp::getIsDeleted, IsDeletedEnum.NO));
        for (ExchangePositionTmp exchangePositionTmp : exchangePositionTmpList) {
            exchangePositionMapper.insert(JSONObject.parseObject(JSONObject.toJSONString(exchangePositionTmp), ExchangePosition.class));
        }
        return Boolean.TRUE;
    }

    public LambdaQueryWrapper<TradeRiskInfo> getLambdaQueryWrapper(TradeRiskInfoDto dto) {
        LambdaQueryWrapper<TradeRiskInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 簿记账户组不为空
        if (CollectionUtils.isNotEmpty(dto.getAssetGroupIds())) {
            List<AssetunitVo> assetunitList = assetUnitClient.getAssetunitByGroupIds(dto.getAssetGroupIds());
            if (CollectionUtils.isNotEmpty(assetunitList)) {
                Set<Integer> assetUnitIds = assetunitList.stream().map(AssetunitVo::getId).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(assetUnitIds)) {
                    lambdaQueryWrapper.in(TradeRiskInfo::getAssetId, assetUnitIds);
                } else { // 为空返回一个查不到数据的wrapper
                    lambdaQueryWrapper.eq(TradeRiskInfo::getAssetId, -1);
                    return lambdaQueryWrapper;
                }
            } else { // 为空返回一个查不到数据的wrapper
                lambdaQueryWrapper.eq(TradeRiskInfo::getAssetId, -1);
                return lambdaQueryWrapper;
            }
        }
        // 簿记账户不为空
        if (CollectionUtils.isNotEmpty(dto.getAssetIds())) {
            lambdaQueryWrapper.in(TradeRiskInfo::getAssetId, dto.getAssetIds());
        }
        // 存续数量 > 0
        if (dto.getAvailableTrade() != null && Boolean.TRUE.equals(dto.getAvailableTrade())) {
            lambdaQueryWrapper.gt(TradeRiskInfo::getAvailableVolume,0);
        } else if (dto.getAvailableTrade() != null && Boolean.FALSE.equals(dto.getAvailableTrade())) {
            // 存续数量 ==0
            lambdaQueryWrapper.eq(TradeRiskInfo::getAvailableVolume,0);
        }

        lambdaQueryWrapper
                .eq(!StringUtils.isEmpty(dto.getSettlementDate()), TradeRiskInfo::getRiskDate, dto.getSettlementDate())
                .eq(!StringUtils.isEmpty(dto.getTradeCode()), TradeRiskInfo::getTradeCode, dto.getTradeCode())
                .in(CollectionUtils.isNotEmpty(dto.getClientIds()), TradeRiskInfo::getClientId, dto.getClientIds())
                .in(CollectionUtils.isNotEmpty(dto.getOptionTypes()), TradeRiskInfo::getOptionType, dto.getOptionTypes())
                .in(CollectionUtils.isNotEmpty(dto.getOptionCombTypes()), TradeRiskInfo::getOptionCombType, dto.getOptionCombTypes())
                .in(CollectionUtils.isNotEmpty(dto.getUnderlyingCodeList()), TradeRiskInfo::getUnderlyingCode, dto.getUnderlyingCodeList())
                .eq(!StringUtils.isEmpty(dto.getTradeRiskCacularResultSourceType()), TradeRiskInfo::getTradeRiskCacularResultSourceType, dto.getTradeRiskCacularResultSourceType())
        ;
        return lambdaQueryWrapper;
    }

    @Override
    public IPage<TradeRiskInfoVo> selectListByPage(TradeRiskInfoDto dto) {
        LambdaQueryWrapper<TradeRiskInfo> lambdaQueryWrapper = getLambdaQueryWrapper(dto);
        IPage<TradeRiskInfo> ipage = tradeRiskInfoMapper.selectPage(new Page<>(dto.getPageNo(), dto.getPageSize()), lambdaQueryWrapper);
        // 所有客户ID
        Set<Integer> clientIds = ipage.getRecords().stream().map(TradeRiskInfo::getClientId).collect(Collectors.toSet());
        // key=客户ID  , value = 客户Obj
        Map<Integer, ClientVO> clientMap = getClientMap(clientIds);
        // 所有簿记id
        Set<Integer> assetIds = ipage.getRecords().stream().map(TradeRiskInfo::getAssetId).collect(Collectors.toSet());
        // key = 簿记ID , value = 簿记Obj
        Map<Integer, AssetunitVo> assetunitMap = getAssetunitMap(assetIds);
        // key=簿记ID  , value = 场内账号名称
        Map<Integer,String> exchangeAccountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(assetIds)) {
            exchangeAccountMap = exchangeAccountClient.getVoByAssetUnitIds(assetIds).stream().collect(Collectors.toMap(ExchangeAccountFeignVO::getAssetunitId, ExchangeAccountFeignVO::getAccount,(v1, v2)->v2));
        }
        // 所有交易编号
        Set<String> tradeCodeSet = ipage.getRecords().stream().map(TradeRiskInfo::getTradeCode).collect(Collectors.toSet());
        LambdaQueryWrapper<TradeMng> tradeMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeMngLambdaQueryWrapper.eq(TradeMng::getIsDeleted,IsDeletedEnum.NO);
        tradeMngLambdaQueryWrapper.in(TradeMng::getTradeCode,tradeCodeSet);
        // key=交易编号 , value = 交易Obj
        Map<String,TradeMng> tradeMngMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tradeCodeSet)){
            tradeMngMap = tradeMngMapper.selectList(tradeMngLambdaQueryWrapper).stream().collect(Collectors.toMap(TradeMng::getTradeCode, item->item,(v1, v2)->v2));
        }
        Map<Integer, String> finalExchangeAccountMap = exchangeAccountMap;
        Map<String, TradeMng> finalTradeMngMap = tradeMngMap;
        return ipage.convert(item -> {
            TradeRiskInfoVo vo = new TradeRiskInfoVo();
            BeanUtils.copyProperties(item, vo);
            vo.setClient(clientMap.get(item.getClientId()));
            vo.setAssetunit(assetunitMap.get(item.getAssetId()));
            if (null != item.getBuyOrSell()) {
                // 客户方向
                vo.setClientBuyOrSell(item.getBuyOrSell().getDesc());
                // 东证方向和客户方向相反
                if (item.getBuyOrSell()== BuyOrSellEnum.buy) {
                    vo.setBuyOrSellName(BuyOrSellEnum.sell.getDesc());
                } else {
                    vo.setBuyOrSellName(BuyOrSellEnum.buy.getDesc());
                }
            }
            if (null != item.getOptionCombType()) {
                vo.setOptionCombTypeName(item.getOptionCombType().getDesc());
            }
            if (null != item.getOptionType()) {
                vo.setOptionTypeName(item.getOptionType().getDesc());
            }
            if (null != item.getCallOrPut()) {
                vo.setCallOrPutName(item.getCallOrPut().getDesc());
            }

            if (null != item.getTradeRiskCacularResultSourceType()) {
                vo.setTradeRiskCacularResultSourceTypeName(item.getTradeRiskCacularResultSourceType().getDesc());
            }
            if (null != item.getTradeRiskCacularResultType()) {
                vo.setTradeRiskCacularResultTypeName(item.getTradeRiskCacularResultType().getDesc());
            }
            if (finalTradeMngMap.containsKey(item.getTradeCode())){
                TradeMng tradeMng = finalTradeMngMap.get(item.getTradeCode());
                vo.setNotionalPrincipal(tradeMng.getNotionalPrincipal());
                vo.setAvailableNotionalPrincipal(tradeMng.getAvailableNotionalPrincipal());
                vo.setRiskVol(tradeMng.getRiskVol());
                if (tradeMng.getSettleType() !=null) {
                    vo.setSettleTypeName(tradeMng.getSettleType().getDesc());
                }
                vo.setBarrier(tradeMng.getBarrier());
                vo.setBasicQuantity(tradeMng.getBasicQuantity());
                vo.setLeverage(tradeMng.getLeverage());
                vo.setKnockoutRebate(tradeMng.getKnockoutRebate());
                vo.setDay1PnL(tradeMng.getDay1PnL());
            }
            vo.setAccount(finalExchangeAccountMap.get(item.getAssetId()));
            vo.setRho(item.getRho());
            vo.setDividendRho(item.getDividendRho());
            return vo;
        });
    }

    @Override
    public List<TradeRiskInfoExportVo> getExportData(TradeRiskInfoDto dto) {
        LambdaQueryWrapper<TradeRiskInfo> lambdaQueryWrapper = getLambdaQueryWrapper(dto);
        List<TradeRiskInfo> tradeRiskInfoList = tradeRiskInfoMapper.selectList(lambdaQueryWrapper);
        Set<Integer> clientIds = tradeRiskInfoList.stream().map(TradeRiskInfo::getClientId).collect(Collectors.toSet());
        // key=客户ID  , value = 客户Obj
        Map<Integer, ClientVO> clientMap = getClientMap(clientIds);
        Set<Integer> assetIds = tradeRiskInfoList.stream().map(TradeRiskInfo::getAssetId).collect(Collectors.toSet());
        // key=簿记ID  , value = 簿记Obj
        Map<Integer, AssetunitVo> assetunitMap = getAssetunitMap(assetIds);
        // key=簿记ID  , value = 场内账号名称
        Map<Integer,String> exchangeAccountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(assetIds)) {
            exchangeAccountMap = exchangeAccountClient.getVoByAssetUnitIds(assetIds).stream().collect(Collectors.toMap(ExchangeAccountFeignVO::getAssetunitId, ExchangeAccountFeignVO::getAccount,(v1, v2)->v2));
        }
        // 所有交易编号
        Set<String> tradeCodeSet = tradeRiskInfoList.stream().map(TradeRiskInfo::getTradeCode).collect(Collectors.toSet());
        LambdaQueryWrapper<TradeMng> tradeMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeMngLambdaQueryWrapper.eq(TradeMng::getIsDeleted,IsDeletedEnum.NO);
        tradeMngLambdaQueryWrapper.in(TradeMng::getTradeCode,tradeCodeSet);
        // key=交易编号 , value = 交易Obj
        // key=交易编号 , value = 交易Obj
        Map<String,TradeMng> tradeMngMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tradeCodeSet)){
            tradeMngMap = tradeMngMapper.selectList(tradeMngLambdaQueryWrapper).stream().collect(Collectors.toMap(TradeMng::getTradeCode, item->item,(v1, v2)->v2));
        }
        Map<Integer, String> finalExchangeAccountMap = exchangeAccountMap;
        Map<String, TradeMng> finalTradeMngMap = tradeMngMap;
        return tradeRiskInfoList.stream().map(item -> {
            TradeRiskInfoExportVo vo = new TradeRiskInfoExportVo();
            BeanUtils.copyProperties(item, vo);
            if (null != clientMap.get(item.getClientId())) {
                vo.setClientName(clientMap.get(item.getClientId()).getName());
            }
            if (null != assetunitMap.get(item.getAssetId())) {
                vo.setAssetunitName(assetunitMap.get(item.getAssetId()).getName());
            }
            if (null != item.getBuyOrSell()) {
                vo.setBuyOrSell(item.getBuyOrSell().getDesc());
            }
            if (null != item.getOptionCombType()) {
                vo.setOptionCombType(item.getOptionCombType().getDesc());
            }
            if (null != item.getOptionType()) {
                vo.setOptionType(item.getOptionType().getDesc());
            }
            if (null != item.getCallOrPut()) {
                vo.setCallOrPut(item.getCallOrPut().getDesc());
            }

            /*if (null != item.getTradeRiskCacularResultSourceType()) {
                vo.setTradeRiskCacularResultSourceType(item.getTradeRiskCacularResultSourceType().getDesc());
            }
            if (null != item.getTradeRiskCacularResultType()) {
                vo.setTradeRiskCacularResultType(item.getTradeRiskCacularResultType().getDesc());
            }*/
            if (finalTradeMngMap.containsKey(item.getTradeCode())){
                TradeMng tradeMng = finalTradeMngMap.get(item.getTradeCode());
                vo.setNotionalPrincipal(getBigDecimal2String(tradeMng.getNotionalPrincipal()));
                vo.setAvailableNotionalPrincipal(getBigDecimal2String(tradeMng.getAvailableNotionalPrincipal()));
                vo.setRiskVol(tradeMng.getRiskVol());
                if (tradeMng.getSettleType() !=null) {
                    vo.setSettleType(tradeMng.getSettleType().getDesc());
                }
                vo.setBarrier(tradeMng.getBarrier());
                vo.setBasicQuantity(tradeMng.getBasicQuantity());
                vo.setLeverage(tradeMng.getLeverage());
                vo.setKnockoutRebate(tradeMng.getKnockoutRebate());
                vo.setDay1PnL(tradeMng.getDay1PnL());
            }
            vo.setTodayPnl(item.getTodayProfitLoss());
            vo.setAccount(finalExchangeAccountMap.get(item.getAssetId()));
            vo.setDeltaLots(getBigDecimalString(item.getDeltaLots()));
            vo.setDeltaCash(getBigDecimalString(item.getDeltaCash()));
            vo.setGammaLots(getBigDecimalString(item.getGammaLots()));
            vo.setGammaCash(getBigDecimalString(item.getGammaCash()));
            vo.setTheta(getBigDecimalString(item.getTheta()));
            vo.setVega(getBigDecimalString(item.getVega()));
            vo.setRho(getBigDecimalString(item.getRho()));
            vo.setDividendRho(getBigDecimalString(item.getDividendRho()));
            vo.setAccumulatedPosition(getBigDecimal2ScaleString(item.getAccumulatedPosition()));
            vo.setAccumulatedPayment(getBigDecimal2ScaleString(item.getAccumulatedPayment()));
            vo.setAccumulatedPnl(getBigDecimal2ScaleString(item.getAccumulatedPnl()));
            vo.setTodayAccumulatedPosition(getBigDecimal2ScaleString(item.getTodayAccumulatedPosition()));
            vo.setTodayAccumulatedPayment(getBigDecimal2ScaleString(item.getTodayAccumulatedPayment()));
            vo.setTodayAccumulatedPnl(getBigDecimal2ScaleString(item.getTodayAccumulatedPnl()));
            vo.setGamma(getBigDecimalString(item.getGamma()));
            vo.setTotalAmount(getBigDecimal2String(item.getTotalAmount())); // 千分位
            vo.setAvailableAmount(getBigDecimal2String(item.getAvailableAmount()));
            vo.setMargin(getBigDecimal2String(item.getMargin()));
            return vo;
        }).collect(Collectors.toList());
    }
    /**
     * 四舍五入,千分位展示
     * @param bigDecimalValue 入参
     * @return 返回值
     */
    public String getBigDecimal2String(BigDecimal bigDecimalValue){
        if (bigDecimalValue != null){
            String pattern = "#,##0.00";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            return decimalFormat.format(bigDecimalValue);
        } else {
            return "";
        }
    }

    /**
     * 获取bigdeciaml 的字符串形式,为0或null , 返回空串
     * @param value 入参
     * @return 返回值
     */
    public String getBigDecimalString(BigDecimal value){
        if (value==null || value.compareTo(BigDecimal.ZERO)==0) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }/**
     * 获取bigdeciaml 的四舍五入保留2位小数的字符串形式,为0或null , 返回空串
     * @param value 入参
     * @return 返回值
     */
    public String getBigDecimal2ScaleString(BigDecimal value){
        if (value==null || value.compareTo(BigDecimal.ZERO)==0) {
            return "";
        } else {
            return String.valueOf(value.setScale(2,RoundingMode.HALF_UP));
        }
    }

    @Override
    public IPage<PositionPageListVo> selectPosListByPage(PositionPageListDto dto) throws Exception {
        List<PositionPageListVo> dataList = getDataList(dto);
        log.debug("待分页数据-positionList--" + dataList.size());
        return doPage(dataList, dto);
    }

    /**
     * 查询历史持仓数据 1. 根据查询入参 , 先查出制定交日期的持仓记录 positionList 2. 根据查询出来的持仓记录 , 获取所有的合约代码, 查询合约信息instrumentList 3.
     * 取合约信息instrumentList中交易类型=期权的标的代码集合, 查询标的信息underlyingManagerList 4. 根据入参和上一步中的标的信息underlyingManagerList ,
     * 过滤合约信息instrumentList 5. 持仓记录和合约信息合并(持仓中的合约信息是否存在,不存在就过滤掉) 6. 分页返回
     * @param dto 入参
     * @return 返回值
     * @throws Exception 异常
     */
    public List<PositionPageListVo> getHistoryPosDataList(PositionPageListDto dto) throws Exception {
        // 根据入参中的簿记账户,簿记账户组查询
        //Map<Integer, AssetunitVo> assetUnitMap = getAssetunitMap(dto);
        LambdaQueryWrapper<ExchangePosition> lambdaQueryWrapper = getPositionLambdaQueryWrapper(dto);
        //1. 根据查询入参查询持仓记录 , 已经过滤掉簿记账户,簿记账户组,买卖方向,交易日期 , 剩下条件需要关联合约表过滤
        List<ExchangePosition> list = exchangePositionMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        log.debug("------根据查询入参查询持仓记录-----" + JSON.toJSONString(list.size()));
        Set<String> instrumentIDSet = list.stream().map(ExchangePosition::getInstrumentID).collect(Collectors.toSet());
        // 2. 根据查询出来的持仓记录 , 获取所有的合约代码
        List<InstrumentInfoVo> instrumentInfoVoList = instrumentClient.getInstrumentInfoByIds(instrumentIDSet);
        log.debug("------持仓记录对应的合约信息-----" + JSON.toJSONString(instrumentInfoVoList.size()));
        Set<String> underlyingInstrIds = instrumentInfoVoList.stream().filter(item -> item.getProductClass() == 2).map(InstrumentInfoVo::getUnderlyingInstrId).collect(Collectors.toSet());
        log.debug("------合约信息中交易类型=期权的-----" + JSON.toJSONString(underlyingInstrIds.size()));
        // 3. 取合约信息中交易类型=期权的合约代码集合 ,查标的信息
        List<UnderlyingManagerVO> underlyingManagerVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(underlyingInstrIds)) {
            underlyingManagerVOList = underlyingManagerClient.getUnderlyingByCodes(underlyingInstrIds);
        }
        log.debug("------标的信息-----" + JSON.toJSONString(underlyingManagerVOList.size()));
        Map<String, InstrumentInfoVo> underlyingManagerMap = getInstrumentMap(underlyingInstrIds);
        //Map<String, UnderlyingManagerVo> underlyingManagerMap = underlyingManagerVoList.stream().collect(Collectors.toMap(item -> item.getUnderlyingCode().toUpperCase(), item -> item, (v1, v2) -> v2));
        // 4. 根据查询条件和标的信息 , 过滤合约信息
        Map<String, InstrumentInfoVo> instrumentInfoMap = getInstrumentInfoMap(dto, instrumentInfoVoList);
        log.debug("------根据查询条件和标的信息 , 过滤合约信息-----" + JSON.toJSONString(instrumentInfoMap.size()));
        // 5. 持仓信息和合约信息核对 , 把持仓记录中的合约代码不在合约信息中的去除
        List<ExchangePosition> filterList = list.stream().filter(item -> instrumentInfoMap.containsKey(item.getInstrumentID().toUpperCase())).collect(Collectors.toList());
        log.debug("------持仓信息和合约信息核对 , 把持仓记录中的合约代码不在合约信息中去除-----" + JSON.toJSONString(filterList.size()));
        // 根据持仓交易中的用户代码查询簿记账户
        Map<String, AssetunitVo> assetunitVoMap = getAssetunitMapByInvestorIDs(filterList.stream().map(ExchangePosition::getInvestorID).collect(Collectors.toSet()));
        log.debug("根据持仓交易中的用户代码查询簿记账户-----" + JSON.toJSONString(assetunitVoMap.size()));
        Map<String, BigDecimal> instrumentClosePriceMap = getInstrumentClosePrice(instrumentInfoMap, dto.getDate());
        //6. 组装vo数据,进行分页
        return buildPositionPageListVo(filterList, instrumentInfoMap, assetunitVoMap, underlyingManagerMap, instrumentClosePriceMap);
    }

    public LambdaQueryWrapper<ExchangePosition> getPositionLambdaQueryWrapper(PositionPageListDto dto) {
        String localDate = dto.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<ExchangePosition> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 买卖方向
        if ("空头".equals(dto.getDirection())) {
            lambdaQueryWrapper.eq(ExchangePosition::getPosiDirection, 3);
        } else if ("多头".equals(dto.getDirection())) {
            lambdaQueryWrapper.eq(ExchangePosition::getPosiDirection, 2);
        }
        // 交易日期
        lambdaQueryWrapper.eq(ExchangePosition::getTradingDay, localDate);
        lambdaQueryWrapper.eq(ExchangePosition::getIsDeleted, IsDeletedEnum.NO);
        // 簿记账户不为空
        if (CollectionUtils.isNotEmpty(dto.getAssetUnitIds())) {
            // 根据簿记账户获取对冲账户
            List<ExchangeAccountFeignVO> exchangeAccountFeignVOS = exchangeAccountClient.getVoByAssetUnitIds(dto.getAssetUnitIds());
            List<String> accounts = exchangeAccountFeignVOS.stream().map(ExchangeAccountFeignVO::getAccount).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(accounts)){
                lambdaQueryWrapper.in(ExchangePosition::getInvestorID, accounts);
            } else {
                lambdaQueryWrapper.eq(ExchangePosition::getInvestorID, "返回一个查不到的wrapper");
                return lambdaQueryWrapper;
            }
        }
        if (CollectionUtils.isNotEmpty(dto.getAssetUnitGroupIds())) {
            Set<Integer> gids = dto.getAssetUnitGroupIds();
            List<AssetunitVo> assetunitVoList = assetUnitClient.getAssetunitByGroupIds(gids); // 根据簿记账户组查询簿记账户信息
            Set<Integer> asseUniteIds = assetunitVoList.stream().map(AssetunitVo::getId).collect(Collectors.toSet());
            if(CollectionUtils.isNotEmpty(asseUniteIds)){ // 簿记账户不为空
                List<ExchangeAccountFeignVO> exchangeAccountFeignVOS = exchangeAccountClient.getVoByAssetUnitIds(asseUniteIds);// 根据簿记账户获取对冲账户
                List<String> accounts = exchangeAccountFeignVOS.stream().map(ExchangeAccountFeignVO::getAccount).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(accounts)){
                    lambdaQueryWrapper.in(ExchangePosition::getInvestorID, accounts);
                } else {
                    lambdaQueryWrapper.eq(ExchangePosition::getInvestorID, "返回一个查不到数据的wrapper");
                    return lambdaQueryWrapper;
                }
            } else {
                lambdaQueryWrapper.eq(ExchangePosition::getInvestorID, "返回一个查不到数据的wrapper");
                return lambdaQueryWrapper;
            }
        }
        return lambdaQueryWrapper;
    }


    /**
     * 从redis中获取所有交易记录 , 组装数据 , 并根据查询条件过滤数据
     * @param dto 入参
     * @return 返回值
     * @throws Exception 异常信息
     */
    public List<PositionPageListVo> getDataList(PositionPageListDto dto) throws Exception {
        // 查询历史持仓
        LocalDate currnetDay = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
        if (null != dto.getDate() && !currnetDay.isEqual(dto.getDate())) {
            return getHistoryPosDataList(dto);
        }
        Set<String> keys = stringRedisTemplate.keys(RedisAdapter.EXCHANGE_POSITION_INFO + "*");
        // 所有的交易记录

        if (keys != null && !keys.isEmpty()) {
            List<PositionPageListVo> positionList = new ArrayList<>();
            Map<String, AssetunitVo> assetunitVoMap = getAssetunitMapByAccounts(keys);
            InstrumentInfoMap instrumentInfoMap = getInstrumentInfoMap(keys);
            Map<String, InstrumentInfoVo> instrumentInfoVoMap = instrumentInfoMap.getInstrumentInfoVoMap();
            Map<String, List<ExchangeRealTimePos>> exchangeRealTimePosMap = instrumentInfoMap.getExchangeRealTimePosMap();
            // 标的合约信息
            Map<String, InstrumentInfoVo> underlyingManagerMap = getInstrumentMap(instrumentInfoVoMap.values().stream().map(InstrumentInfoVo::getInstrumentId).collect(Collectors.toSet()));
            for (String key : keys) {
                List<ExchangePosition> tempList = CglibUtil.copyList(exchangeRealTimePosMap.get(key), ExchangePosition::new);
                positionList.addAll(buildPositionPageListVo(tempList, instrumentInfoVoMap, assetunitVoMap, underlyingManagerMap, getLastPriceMap(instrumentInfoVoMap)));
            }
            log.debug("分页查询入参=" + JSON.toJSON(dto));
            return positionList.stream().filter(item -> {
                        // 簿记账户过滤条件
                        boolean assetUnitFlag = true;
                        if (!CollectionUtils.isEmpty(dto.getAssetUnitIds())
                                && !dto.getAssetUnitIds().contains(item.getAssetUnitId())
                        ) {
                            log.debug("簿记账户过滤条件" + JSON.toJSONString(dto.getAssetUnitIds()));
                            assetUnitFlag = false;
                        }
                        // 簿记账户组过滤条件
                        boolean assetUnitFroupFlag = true;
                        if (!CollectionUtils.isEmpty(dto.getAssetUnitGroupIds())
                                && !dto.getAssetUnitGroupIds().contains(item.getAssetUnitGroupId())
                        ) {
                            log.debug("簿记账户组过滤条件" + JSON.toJSONString(dto.getAssetUnitGroupIds()));
                            assetUnitFroupFlag = false;
                        }
                        // 标的代码过滤条件
                        AtomicBoolean underlyingCodeFlag = new AtomicBoolean(true);
                        if (!CollectionUtils.isEmpty(dto.getUnderlyingCodes())) {
                            underlyingCodeFlag.set(false);
                            dto.getUnderlyingCodes().forEach(code -> {
                                if (code.equalsIgnoreCase(item.getUnderlyingCode())) {
                                    underlyingCodeFlag.set(true);
                                }
                            });
                            log.debug("标的代码过滤条件" + JSON.toJSONString(dto.getUnderlyingCodes()));
                        }
                        // 期权代码过滤条件
                        boolean optionCodeFlag = true;
                        if (!StringUtils.isEmpty(dto.getOptionCode())
                                && !dto.getOptionCode().equals(item.getOptionCode())
                        ) {
                            log.debug("期权代码过滤条件" + JSON.toJSONString(dto.getOptionCode()));
                            optionCodeFlag = false;
                        }
                        // 交易类型过滤条件
                        boolean traderType = true;
                        if (null != dto.getTraderType()) {
                            log.debug("交易类型过滤条件" + JSON.toJSONString(dto.getTraderType()));
                            if (dto.getTraderType() == 1) { // 期货
                                traderType = TradeRiskCacularResultType.european.getDesc().equals(item.getTradeType());
                            } else {
                                traderType = TradeRiskCacularResultType.option.getDesc().equals(item.getTradeType());
                            }
                        }

                        // 买卖方向
                        boolean direction = true;
                        if (!StringUtils.isEmpty(dto.getDirection())
                                && !dto.getDirection().equals(item.getDirection())
                        ) {
                            log.debug("买卖方向" + JSON.toJSONString(dto.getDirection()));
                            direction = false;
                        }
                        return assetUnitFlag && assetUnitFroupFlag && underlyingCodeFlag.get() && optionCodeFlag && traderType && direction;
                    }
            ).collect(Collectors.toList());
        }
        // 对positionList进行条件查询
        return new ArrayList<>();
    }

    /**
     * 根据合约id获取合约信息 key=合约id , value=合约信息
     * @param instIDs 入参
     * @return 返回值
     */
    public Map<String, InstrumentInfoVo> getInstrumentMap(Set<String> instIDs) {
        if (CollectionUtils.isEmpty(instIDs)) {
            return new HashMap<>();
        }
        List<InstrumentInfoVo> list = instrumentClient.getInstrumentInfoByIds(instIDs);
        return list.stream().collect(Collectors.toMap(item -> item.getInstrumentId().toUpperCase(), item -> item, (v1, v2) -> v2));
    }

    // 分页操作
    public Page<PositionPageListVo> doPage(List<PositionPageListVo> dataList, PositionPageListDto dto) {
        log.debug("------过滤之后的数组-----" + dataList.size());
        int totalcount = dataList.size(); // 总条数
        int pageCount = 0; // 总页数
        List<String> subList;// 当前页数据
        int m = totalcount % dto.getPageSize();
        if (m > 0) {
            pageCount = totalcount / dto.getPageSize() + 1;
        } else {
            pageCount = totalcount / dto.getPageSize();
        }
        List<PositionPageListVo> list = dataList.stream().skip((long) (dto.getPageNo() - 1) * dto.getPageSize()).limit(dto.getPageSize()).collect(Collectors.toList());
        log.debug("--分页后数据---" + list.size());
        Page<PositionPageListVo> page = new Page<>();
        page.setRecords(list);
        page.setTotal(totalcount);
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getPageNo());
        return page;
    }

    /**
     * 历史持仓中组装数据
     * @param positionList            持仓数据
     * @param instrumentInfoMap       合约信息
     * @param accountAssetUnitMap     簿记账户信息
     * @param underlyingManagerMap    标的信息
     * @param instrumentClosePriceMap 标的现价
     * @return 返回值
     */
    private List<PositionPageListVo> buildPositionPageListVo(List<ExchangePosition> positionList, Map<String, InstrumentInfoVo> instrumentInfoMap,
                                                             Map<String, AssetunitVo> accountAssetUnitMap, Map<String, InstrumentInfoVo> underlyingManagerMap,
                                                             Map<String, BigDecimal> instrumentClosePriceMap) {
        List<PositionPageListVo> voList = new ArrayList<>();
        positionList.forEach(item -> {
            PositionPageListVo vo = new PositionPageListVo();
            if (accountAssetUnitMap.containsKey(item.getInvestorID())) {
                vo.setAssetUnitName(accountAssetUnitMap.get(item.getInvestorID()).getName());
                vo.setAssetUnitId(accountAssetUnitMap.get(item.getInvestorID()).getId());
                vo.setAssetUnitGroupId(accountAssetUnitMap.get(item.getInvestorID()).getGroupId());
            }
            if (instrumentInfoMap.containsKey(item.getInstrumentID().toUpperCase())) {
                BigDecimal closePrice = BigDecimal.ZERO;
                InstrumentInfoVo instInfo = instrumentInfoMap.get(item.getInstrumentID().toUpperCase());
                log.debug("instInfo-----------------" + JSON.toJSONString(instInfo));
                if (1 == instInfo.getProductClass()) {
                    vo.setTradeType(TradeRiskCacularResultType.european.getDesc());
                    vo.setUnderlyingCode(instInfo.getInstrumentId());
                    vo.setUnderlyingName(instInfo.getInstrumentName());
                    if (instrumentClosePriceMap.containsKey(instInfo.getInstrumentId().toUpperCase())) {
                        closePrice = instrumentClosePriceMap.get(instInfo.getInstrumentId().toUpperCase());
                    }
                    log.debug("标的代码---------1--------" + JSON.toJSONString(instInfo.getInstrumentId()) + "--closePrice--" + JSON.toJSONString(instrumentClosePriceMap.get(instInfo.getInstrumentId().toUpperCase())));
                } else if (2 == instInfo.getProductClass()) {
                    vo.setTradeType(TradeRiskCacularResultType.option.getDesc());
                    vo.setUnderlyingCode(instInfo.getUnderlyingInstrId());
                    if (underlyingManagerMap.containsKey(instInfo.getUnderlyingInstrId().toUpperCase())) {
                        InstrumentInfoVo underlyingManagerVo = underlyingManagerMap.get(instInfo.getUnderlyingInstrId().toUpperCase());
                        vo.setUnderlyingName(underlyingManagerVo.getInstrumentName());
                    }
                    if (instrumentClosePriceMap.containsKey(instInfo.getUnderlyingInstrId().toUpperCase())) {
                        closePrice = instrumentClosePriceMap.get(instInfo.getUnderlyingInstrId().toUpperCase());
                    }
                    log.debug("标的代码---------2--------" + JSON.toJSONString(instInfo.getUnderlyingInstrId()) + "--closePrice--" + JSON.toJSONString(instrumentClosePriceMap.get(instInfo.getUnderlyingInstrId().toUpperCase())));
                }
                vo.setOptionCode(item.getInstrumentID());
                vo.setPositionCount(item.getPosition() * instInfo.getVolumeMultiple());
                vo.setUnderlyingPrice(closePrice);
            }
            vo.setPosition(item.getPosition());
            if ("3".equals(item.getPosiDirection())) {
                vo.setDirection("空头");
            } else if ("2".equals(item.getPosiDirection())) {
                vo.setDirection("多头");
            }
            vo.setYdPosition(item.getYdPosition());
            voList.add(vo);
        });
        return voList;
    }

    /**
     * 获取合约最新价格
     * @param instrumentInfoVoMap 合约集合
     * @return 最新价格
     */
    private Map<String, BigDecimal> getLastPriceMap(Map<String, InstrumentInfoVo> instrumentInfoVoMap) {

        Set<String>  underlyingCodeSet= new HashSet<>();
        for (InstrumentInfoVo vo : instrumentInfoVoMap.values()) {
            if (1 == vo.getProductClass()) {
                underlyingCodeSet.add(vo.getInstrumentId().toUpperCase());
            } else if (2 == vo.getProductClass()) {
                underlyingCodeSet.add(vo.getUnderlyingInstrId().toUpperCase());
            }
        }
        return marketClient.getLastPriceByUnderlyingCodeList(underlyingCodeSet);
    }

    @Override
    public void exportPos(PositionPageListDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<PositionPageListVo> list = getDataList(dto);
        List<PositionExportVo> exportList = list.stream().map(item -> {
            PositionExportVo vo = new PositionExportVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        log.debug("------------持仓导出记录总条数-----" + exportList.size());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        // 文件名称 = 风险导出+时间戳
        String fileName = "持仓记录导出"+sdf.format(calendar.getTime())+calendar.get(Calendar.MILLISECOND);
        HutoolUtil.export(exportList, fileName, "持仓记录", PositionExportVo.class, request, response);
    }

    /**
     * 获取风险分页返回结果中对应的客户信息 key = 客户ID , value = 客户信息
     * @param clientIds 入参
     * @return 返回值
     */
    public Map<Integer, ClientVO> getClientMap(Set<Integer> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return new HashMap<>();
        }
        List<ClientVO> list = client.getClientListByIds(clientIds);
        return list.stream().collect(Collectors.toMap(ClientVO::getId, item -> item, (v1, v2) -> v2));
    }


    /**
     * 获取簿记账户对应对冲账户 , 返回map , key=account , value = 簿记账户 key=对冲账户account,value=簿记账户
     * @param assetUnitMap 入参
     * @return 返回值
     */
    public Map<String, AssetunitVo> getAccountAssetunitMap(Map<Integer, AssetunitVo> assetUnitMap) {
        Set<Integer> asseUniteIds = assetUnitMap.keySet();
        List<ExchangeAccountFeignVO> exchangeAccountFeignVOList = exchangeAccountClient.getVoByAssetUnitIds(asseUniteIds);
        return exchangeAccountFeignVOList.stream().collect(Collectors.toMap(ExchangeAccountFeignVO::getAccount, item -> assetUnitMap.get(item.getAssetunitId())));
    }

    /**
     * 获取簿记账户信息 key = 簿记账户ID , value = 簿记账户信息
     * @param assetIds 入参
     * @return 返回值
     */
    public Map<Integer, AssetunitVo> getAssetunitMap(Set<Integer> assetIds) {
        if (CollectionUtils.isEmpty(assetIds)) {
            return new HashMap<>();
        }
        List<AssetunitVo> list = assetUnitClient.getAssetUnitList(assetIds);
        return list.stream().collect(Collectors.toMap(AssetunitVo::getId, item -> item, (v1, v2) -> v2));
    }

    /**
     * 获取对冲账户对应的簿记账户信息 key = 对冲账户account , value = 簿记账户信息
     * @param keys 入参
     * @return 返回值
     */
    public Map<String, AssetunitVo> getAssetunitMapByAccounts(Set<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new HashMap<>();
        }
        Set<String> accounts = new HashSet<>();
        keys.forEach(key -> {
            String[] split = key.split(":");
            String account = split[split.length - 1];
            accounts.add(account);
        });
        return assetUnitClient.getMapByAccounts(accounts);
    }

    /**
     * 获取对冲账户对应的簿记账户信息 key = 对冲账户account , value = 簿记账户组信息
     * @param investorIDs 入参
     * @return 返回值
     */
    public Map<String, AssetunitVo> getAssetunitMapByInvestorIDs(Set<String> investorIDs) {
        if (CollectionUtils.isEmpty(investorIDs)) {
            return new HashMap<>();
        }
        log.debug("---------getAssetunitMapByInvestorIDs--=" + JSONObject.toJSONString(investorIDs));
        return assetUnitClient.getMapByAccounts(investorIDs);
    }

    /**
     * 获取簿记账户组信息 key = 簿记账户组id,value = 簿记账户组
     * @param list 入参
     * @return 返回值
     */
    public Map<Integer, AssetunitGroupVo> assetunitGroupMap(List<AssetunitVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        Map<Integer, AssetunitGroupVo> map = new HashMap<>();
        Set<Integer> gids = list.stream().map(AssetunitVo::getGroupId).collect(Collectors.toSet());
        List<AssetunitGroupVo> assetunitGroupList = assetUnitClient.getAssetUnitGroupByIds(gids);
        list.forEach(assetunitVo -> {
            Integer id = assetunitVo.getId();
            Integer gid = assetunitVo.getGroupId();
            AssetunitGroupVo gvo = assetunitGroupList.stream().filter(item -> item.getId().equals(gid)).findFirst().get();
            map.put(id, gvo);
        });
        return map;
    }

    /**
     * 根据簿记账户id,簿记账户组id查询 key = 簿记账户id , value = 簿记账户
     * @param dto 入参
     * @return 返回值
     */
    public Map<Integer, AssetunitVo> getAssetunitMap(PositionPageListDto dto) {
        List<AssetunitVo> assetunitVoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getAssetUnitGroupIds())) {
            Set<Integer> gids = dto.getAssetUnitGroupIds();
            if (CollectionUtils.isEmpty(gids)) {
                return new HashMap<>();
            }
            List<AssetunitVo> tempList = assetUnitClient.getAssetunitByGroupIds(gids);
            if (null != tempList && !tempList.isEmpty()) {
                assetunitVoList.addAll(tempList);
            }
        }
        if (CollectionUtils.isNotEmpty(dto.getAssetUnitIds())) {
            Set<Integer> ids = dto.getAssetUnitIds();
            List<AssetunitVo> tempList = assetUnitClient.getAssetUnitList(ids);
            if (null != tempList && !tempList.isEmpty()) {
                assetunitVoList.addAll(tempList);
            }
        }
        return assetunitVoList.stream().collect(Collectors.toMap(AssetunitVo::getId, item -> item, (v1, v2) -> v2));
    }

    /**
     * 获取合约代码对应的标的价格 key = 合约代码(区分期货,期权),value = 标的价格
     * @param instrumentInfoMap 入参
     * @param date 入参
     * @return 返回值
     */
    public Map<String, BigDecimal> getInstrumentClosePrice(Map<String, InstrumentInfoVo> instrumentInfoMap, LocalDate date) {
        CloseDatePriceByDateDto dto = new CloseDatePriceByDateDto();
        Set<String> underlyingCodes = new HashSet<>();
        instrumentInfoMap.values().forEach(item -> {
            int productClass = item.getProductClass();
            if (1 == productClass) {
                underlyingCodes.add(item.getInstrumentId());
            } else if (2 == productClass) {
                underlyingCodes.add(item.getUnderlyingInstrId());
            }
        });
        dto.setDate(date);
        dto.setUnderlyingCodes(underlyingCodes);
        if (CollectionUtils.isEmpty(underlyingCodes)) {
            return new HashMap<>();
        }
        return marketClient.getClosePriceByDateAndCode(dto);
    }

    /**
     * 获取redsi中的持仓信息,并根据持仓信息获取合约信息,组装数据返回
     * @param keys 入参
     * @return 返回值
     */
    public InstrumentInfoMap getInstrumentInfoMap(Set<String> keys) {
        InstrumentInfoMap returnMap = new InstrumentInfoMap();
        Map<String, List<ExchangeRealTimePos>> exchangeRealTimePosMap = new HashMap<>();
        Set<String> instrumentSet = new HashSet<>();
        keys.forEach(key -> {
            List<Object> values = stringRedisTemplate.opsForHash().values(key);
            List<ExchangeRealTimePos> list = JSONArray.parseArray(values.toString(), ExchangeRealTimePos.class);
            exchangeRealTimePosMap.put(key, list);
            list.forEach(item -> instrumentSet.add(item.getInstrumentID()));
        });
        if (CollectionUtils.isEmpty(instrumentSet)) {
            return returnMap;
        }
        // 根据instrumentId取合约信息
        List<InstrumentInfoVo> instrumentInfoList = instrumentClient.getInstrumentInfoByIds(instrumentSet);
        Map<String, InstrumentInfoVo> instrumentInfoVoMap = instrumentInfoList.stream().collect(Collectors.toMap(item -> item.getInstrumentId().toUpperCase(), item -> item, (v1, v2) -> v2));
        returnMap.setExchangeRealTimePosMap(exchangeRealTimePosMap);
        returnMap.setInstrumentInfoVoMap(instrumentInfoVoMap);
        return returnMap;
    }

    /**
     * 获取合约信息 key = 合约代码 , value = 合约信息
     * @param dto 入参
     * @param instrumentInfoVoList 入参
     * @return 返回值
     */
    public Map<String, InstrumentInfoVo> getInstrumentInfoMap(PositionPageListDto dto, List<InstrumentInfoVo> instrumentInfoVoList) {

        List<InstrumentInfoVo> instrumentInfoVoFilterList = instrumentInfoVoList.stream().filter(item -> {
            // 交易类型过滤
            boolean traderTypeFlag = true;
            if (null != dto.getTraderType()) {
                traderTypeFlag = item.getProductClass() == dto.getTraderType();
            }
            // 期权代码过滤
            boolean optionCodeFlag = true;
            if (!StringUtils.isEmpty(dto.getOptionCode())) {
                optionCodeFlag = item.getInstrumentId().equalsIgnoreCase(dto.getOptionCode());
            }
            // 标的代码过滤
            AtomicBoolean underlyingCodeFlag = new AtomicBoolean(true);
            if (CollectionUtils.isNotEmpty(dto.getUnderlyingCodes())) {
                underlyingCodeFlag.set(false);
                if (item.getProductClass() == 1) {
                    dto.getUnderlyingCodes().forEach(code -> {
                        if (code.equalsIgnoreCase(item.getInstrumentId())) {
                            underlyingCodeFlag.set(true);
                        }
                    });
                } else if (item.getProductClass() == 2) {
                    dto.getUnderlyingCodes().forEach(code -> {
                        if (code.equalsIgnoreCase(item.getUnderlyingInstrId())) {
                            underlyingCodeFlag.set(true);
                        }
                    });
                }
            }
            return traderTypeFlag && optionCodeFlag && underlyingCodeFlag.get();
        }).collect(Collectors.toList());
        return instrumentInfoVoFilterList.stream().collect(Collectors.toMap(item -> item.getInstrumentId().toUpperCase(), item -> item, (v1, v2) -> v2));
    }

    /**
     * 需要key的尾部对应对冲账户的account字段 , 查询对冲账户中对应簿记账户
     * @param key 入参
     * @return 返回值
     *
     * @throws Exception 异常
     */
    public AssetunitVo getExchangeAccountName(String key) throws Exception {
        String[] split = key.split(":");
        String account = split[split.length - 1];
        ExchangeAccountQueryDto exchangeAccountQueryDto = new ExchangeAccountQueryDto();
        exchangeAccountQueryDto.setAccount(account);
        ExchangeAccountFeignVO exchangeAccountFeignVO = exchangeAccountClient.getVoByname(exchangeAccountQueryDto);
        if (null != exchangeAccountFeignVO) {
            return assetUnitClient.getAssetunitById(exchangeAccountFeignVO.getAssetunitId());
        } else {
            throw new Exception("未查询到当前持仓对应的簿记账户信息,对冲账户account=" + account);
        }
    }

    @Override
    public void getExportDefinitionRisk(TradeRiskInfoDto dto,HttpServletRequest request, HttpServletResponse response) {
        LambdaQueryWrapper<TradeRiskInfo> lambdaQueryWrapper = getLambdaQueryWrapper(dto);
        List<TradeRiskInfo> tradeRiskInfoList = tradeRiskInfoMapper.selectList(lambdaQueryWrapper);
        List<DefinitionTradeRiskInfoExportVo> returnList = CglibUtil.copyList(tradeRiskInfoList,DefinitionTradeRiskInfoExportVo::new,(db,vo)->{
            BeanUtils.copyProperties(db,vo);
            vo.setTradeCode(db.getTradeCode());
            vo.setRiskDate(db.getRiskDate());
            vo.setAvailableAmount(getBigDecimal2String(db.getAvailableAmount()));
            vo.setMargin(getBigDecimal2String(db.getMargin()));
            vo.setDelta(db.getDelta());
            vo.setGamma(db.getGamma());
            vo.setTheta(db.getTheta());
            vo.setRho(db.getRho());
        });
        if(null == returnList || returnList.isEmpty()){
            BussinessException.E_300101.assertTrue(Boolean.FALSE);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            // 文件名称 = 风险导出+时间戳
            String fileName = "自定义风险导出"+sdf.format(calendar.getTime());
            HutoolUtil.export(returnList,fileName,"自定义风险导出",DefinitionTradeRiskInfoExportVo.class,request,response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
