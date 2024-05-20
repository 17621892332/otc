package org.orient.otc.quote.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.dm.dto.TradayAddDaysDto;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dm.feign.InstrumentClient;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.market.vo.MarketInfoVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.redisstream.RedisStreamInterface;
import org.orient.otc.common.redisstream.RedisStreamTemplate;
import org.orient.otc.common.redisstream.enums.StreamTypeEnum;
import org.orient.otc.quote.entity.ExchangePositionTmp;
import org.orient.otc.quote.entity.ExchangeTrade;
import org.orient.otc.quote.entity.ExchangeTradeTmp;
import org.orient.otc.quote.enums.ExchangeEodType;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.ExchangePositionTmpMapper;
import org.orient.otc.quote.mapper.ExchangeTradeMapper;
import org.orient.otc.quote.mapper.ExchangeTradeTmpMapper;
import org.orient.otc.quote.service.RiskService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * 交易与持仓信息
 */
@Component
@Slf4j
public class MyRedisStreamImpl implements RedisStreamInterface {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ExchangeTradeMapper exchangeTradeMapper;
    @Resource
    private InstrumentClient instrumentClient;

    @Resource
    private CalendarClient calendarClient;
    @Resource
    private RedissonClient redissonClient;

    @Value("${isCaclTradePos: false}")
    private Boolean isCaclTradePos;

    @Resource
    private MarketClient marketClient;
    @Resource
    private RiskService riskService;
    @Resource
    private ExchangeTradeTmpMapper exchangeTradeTmpMapper;

    @Resource
    private ExchangePositionTmpMapper exchangePositionTmpMapper;

    @Override
    public void handData(Map<String, String> data) {
        if (data.containsKey("ctpmsg")) {
            String ctpmsg = data.get("ctpmsg");
            JSONObject jsonObject = JSONObject.parseObject(ctpmsg);
            if (jsonObject.containsKey("type")) {
                Integer type = jsonObject.getInteger("type");
                if (type == 2) {
                    RLock lock = redissonClient.getFairLock("exchangeCalcPos"); //这个锁是为了和重新计算互斥，重新计算持仓的时候这里不能再进行计算了
                    lock.lock();
                    try {
                        JSONObject content = jsonObject.getJSONObject("data");
                        ExchangeTrade tradeData;
                        tradeData = content.toJavaObject(ExchangeTrade.class);
                        //如果是补单的将工作日调整为上一个工作日
                        if (tradeData.getOrderSysID().contains("_")) {
                            tradeData.setTradingDay(getLastTradingDay(tradeData.getTradingDay()));
                        }
                        ExchangeTrade exchangeTrade = exchangeTradeMapper.selectOne(new LambdaQueryWrapper<ExchangeTrade>()
                                .eq(ExchangeTrade::getTradeID, tradeData.getTradeID())
                                .eq(ExchangeTrade::getDirection, tradeData.getDirection())
                                .eq(ExchangeTrade::getInstrumentID, tradeData.getInstrumentID())
                                .eq(ExchangeTrade::getExchangeID, tradeData.getExchangeID())
                                .eq(ExchangeTrade::getTradingDay, tradeData.getTradingDay()));
                        if (Objects.nonNull(exchangeTrade)) {
                            return;
                        }
                        //查询期货合约信息(微服务内部请求)，设置合约的基本信息
                        InstrumentInfoVo instInfo = instrumentClient.getInstrumentInfo(tradeData.getInstrumentID());
                        String underlyingCode;
                        if (instInfo.getProductClass() == 1) {
                            underlyingCode = instInfo.getInstrumentId();
                        } else {
                            underlyingCode = instInfo.getUnderlyingInstrId();
                        }
                        //获取实时行情
                        MarketInfoVO marketInfoVO = marketClient.getLastMarketDataByCode(underlyingCode.toUpperCase());
                        tradeData.setTradingFuturesPrice(marketInfoVO.getLastPrice());
                        //成交数据入库mysql
                        exchangeTradeMapper.insert(tradeData);
                        riskService.calcPos(tradeData);
                        setOpenOrCloseAmount(tradeData, instInfo);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        lock.unlock();
                    }
                } else if (type == 3) {
                    //手动获取持仓记录
                    if (Objects.nonNull(jsonObject.get("data")) && !jsonObject.getJSONObject("data").isEmpty()) {
                        JSONObject content = jsonObject.getJSONObject("data");
                        ExchangePositionTmp posData;
                        posData = content.toJavaObject(ExchangePositionTmp.class);
                        QueryWrapper<ExchangePositionTmp> qryWrapper = new QueryWrapper<>();
                        qryWrapper.eq("tradingDay", posData.getTradingDay())
                                .eq("investorID", posData.getInvestorID())
                                .eq("instrumentID", posData.getInstrumentID())
                                .eq("posiDirection", posData.getPosiDirection())
                                .eq("isDeleted", 0);
                        ExchangePositionTmp posUpdate = exchangePositionTmpMapper.selectOne(qryWrapper);
                        if (Objects.nonNull(posUpdate)) {
                            exchangePositionTmpMapper.update(posData, qryWrapper);
                        } else {
                            exchangePositionTmpMapper.insert(posData);
                        }
                    } else {
                        //消息处理完
                        String userId = jsonObject.getString("userId");//场内账号
                    }
                } else if (type == 5) {
                    //手动获取交易记录
                    if (Objects.nonNull(jsonObject.get("data")) && !jsonObject.getJSONObject("data").isEmpty()) {
                        JSONObject content = jsonObject.getJSONObject("data");
                        ExchangeTradeTmp tradeData;
                        tradeData = content.toJavaObject(ExchangeTradeTmp.class);
                        //如果是补单的将工作日调整为上一个工作日
                        if (tradeData.getOrderSysID().contains("_")) {
                            tradeData.setTradingDay(getLastTradingDay(tradeData.getTradingDay()));
                        }
                        LambdaQueryWrapper<ExchangeTradeTmp> query = new LambdaQueryWrapper<ExchangeTradeTmp>()
                                .eq(ExchangeTradeTmp::getTradeID, tradeData.getTradeID())
                                .eq(ExchangeTradeTmp::getDirection, tradeData.getDirection())
                                .eq(ExchangeTradeTmp::getInstrumentID, tradeData.getInstrumentID())
                                .eq(ExchangeTradeTmp::getExchangeID, tradeData.getExchangeID())
                                .eq(ExchangeTradeTmp::getTradingDay, tradeData.getTradingDay());
                        ExchangeTradeTmp exchangeTrade = exchangeTradeTmpMapper.selectOne(query);
                        if (Objects.isNull(exchangeTrade)) {
                            exchangeTradeTmpMapper.insert(tradeData);
                        } else {
                            exchangeTradeTmpMapper.update(tradeData, query);
                        }
                    }
                }
            }
        }
    }

    private String getLastTradingDay(String tradingDay) {
        TradayAddDaysDto tradayAddDaysDto = new TradayAddDaysDto();
        tradayAddDaysDto.setDate(LocalDate.parse(tradingDay, DateTimeFormatter.ofPattern("yyyyMMdd")));
        tradayAddDaysDto.setDays(-1);
        return calendarClient.tradeDayAddDays(tradayAddDaysDto).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 空头(SHORT)平仓 : direction:0   offsetFlag：非0<br/> 空头(SHORT)开仓 : direction:1   offsetFlag：0<br/> 多头(LONG)平仓 :
     * direction:1   offsetFlag：非0<br/> 多头(LONG)开仓 : direction:0   offsetFlag：0<br/> 设置今日开平仓金额<br/>
     * @param exchangeTrade 交易记录
     */
    private void setOpenOrCloseAmount(ExchangeTrade exchangeTrade, InstrumentInfoVo instInfo) {
        if ("0".equals(exchangeTrade.getOffsetFlag())) {
            //direction买卖方向 0:买1:卖
            //开仓时: direction:1对应空头(SHORT)开仓，direction:0对应多头(LONG)开仓
            String redisKey = exchangeTrade.getInvestorID() + "_" + exchangeTrade.getInstrumentID() + "_" + ("0".equals(exchangeTrade.getDirection()) ? ExchangeEodType.LONG.name() : ExchangeEodType.SHORT.name());
            log.trace("处理场内交易记录:{},交易详情:{}", redisKey, JSONObject.toJSONString(exchangeTrade));

            Object openAmountObj = stringRedisTemplate.opsForHash().get(RedisAdapter.TODAY_OPEN_TRADE_AMOUNT + exchangeTrade.getTradingDay(), redisKey);
            BigDecimal openAmount = BigDecimal.ZERO;
            if (openAmountObj != null) {
                openAmount = new BigDecimal(openAmountObj.toString());
            }
            log.trace("openAmount:{}", openAmount);
            openAmount = openAmount.add(BigDecimal.valueOf(exchangeTrade.getPrice() * exchangeTrade.getVolume() * instInfo.getVolumeMultiple()));
            stringRedisTemplate.opsForHash().put(RedisAdapter.TODAY_OPEN_TRADE_AMOUNT + exchangeTrade.getTradingDay(), redisKey, openAmount.toString());

        }
        if (!"0".equals(exchangeTrade.getOffsetFlag())) {

            //平仓时: direction:1对应多头(LONG)平仓，direction:0对应空头(SHORT)平仓
            String redisKey = exchangeTrade.getInvestorID() + "_" + exchangeTrade.getInstrumentID() + "_" + ("1".equals(exchangeTrade.getDirection()) ? ExchangeEodType.LONG.name() : ExchangeEodType.SHORT.name());
            log.trace("处理场内交易记录:{},交易详情:{}", redisKey, JSONObject.toJSONString(exchangeTrade));
            Object closeAmountObj = stringRedisTemplate.opsForHash().get(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT + exchangeTrade.getTradingDay(), redisKey);
            BigDecimal closeAmount = BigDecimal.ZERO;
            if (closeAmountObj != null) {
                closeAmount = new BigDecimal(closeAmountObj.toString());
            }
            log.trace("closeAmount:{}", closeAmount);
            closeAmount = closeAmount.add(BigDecimal.valueOf(exchangeTrade.getPrice() * exchangeTrade.getVolume() * instInfo.getVolumeMultiple()));
            log.trace("closeAmount:{}", closeAmount);
            stringRedisTemplate.opsForHash().put(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT + exchangeTrade.getTradingDay(), redisKey, closeAmount.toString());
        }
    }

    @Override
    public void setTemplate(RedisStreamTemplate redisStreamTemplate) {
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            BussinessException.E_300102.doThrow();
        }
        redisStreamTemplate.setTopicName("mq_trade_pos");
        redisStreamTemplate.setGroup("risk");
        redisStreamTemplate.setConsumer(host);
        redisStreamTemplate.setStreamType(StreamTypeEnum.pull);
        redisStreamTemplate.setBatchSize(1);
        if (!isCaclTradePos) {
            redisStreamTemplate.setIsOpen(Boolean.FALSE);
        }
    }

}
