package org.orient.otc.quote.service.impl;

import cn.hutool.core.date.StopWatch;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.dm.dto.CalendarPropertyQueryDto;
import org.orient.otc.api.dm.dto.TradayAddDaysDto;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.CalendarProperty;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.dto.MarginQuoteDTO;
import org.orient.otc.api.dto.MarinTradeDataDTO;
import org.orient.otc.api.dto.TradeObsDateDTO;
import org.orient.otc.api.dto.VolatityDataDTO;
import org.orient.otc.api.feign.PythonClient;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.quote.dto.risk.TradeRiskCacularResult;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.api.user.feign.ExchangeAccountClient;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.api.vo.MarginVO;
import org.orient.otc.api.vo.PythonResult;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.exception.BaseException;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.cron.RiskCacular;
import org.orient.otc.quote.cron.UnderlyingByRisk;
import org.orient.otc.quote.dto.quote.QuoteCalculateDTO;
import org.orient.otc.quote.dto.quote.QuoteCalculateDetailDTO;
import org.orient.otc.quote.dto.risk.ExchangeRealTimePos;
import org.orient.otc.quote.dto.trade.TradeInsertDTO;
import org.orient.otc.quote.dto.trade.TradeMngDTO;
import org.orient.otc.quote.dto.volatility.VolatityDataDto;
import org.orient.otc.quote.entity.*;
import org.orient.otc.quote.enums.OpenOrCloseEnum;
import org.orient.otc.quote.enums.TradeTypeEnum;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.*;
import org.orient.otc.quote.service.*;
import org.orient.otc.quote.util.QuoteUtil;
import org.orient.otc.quote.vo.quote.QuoteStringResultVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 结算服务实现
 */
@Service
@Slf4j
public class SettlementServiceImpl implements SettlementService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TradeMngService tradeMngService;


    @Resource
    private RedissonClient redissonClient;

    @Resource
    private QuoteService quoteService;

    @Resource
    private ExchangePositionService exchangePositionService;
    @Resource
    private ClientClient clientClient;
    @Resource
    private CalendarClient calendarClient;


    @Resource
    private ExchangeAccountClient exchangeAccountClient;

    @Resource
    private RiskCacular riskCacular;

    @Resource
    private ExchangeTradeMapper exchangeTradeMapper;

    @Resource
    private UnderlyingManagerClient underlyingManagerClient;

    @Resource
    private MarketClient marketClient;

    @Resource
    private ExchangePositionCheckMapper exchangePositionCheckMapper;

    @Resource
    private TradeObsDateMapper tradeObsDateMapper;

    @Resource
    private TradeRiskInfoService tradeRiskInfoService;
    @Resource
    private ExchangeTradeTmpMapper exchangeTradeTmpMapper;
    @Resource
    private ExchangePositionTmpMapper exchangePositionTmpMapper;

    @Resource
    private ObsTradeDetailMapper obsTradeDetailMapper;
    @Resource
    private RiskService riskService;
    @Resource
    private TradeMngMapper tradeMngMapper;
    @Resource
    private TradeSnowballOptionMapper tradeSnowballOptionMapper;

    @Resource
    private SnowKnockedinLogMapper snowKnockedinLogMapper;

    @Resource
    private PythonClient pythonClient;

    @Resource
    private SystemConfigUtil systemConfigUtil;

    @Resource
    private VolatilityService volatilityService;

    @Override
    public Boolean getExchangeTrade() {
        //清空表中数据
        ExchangeTradeTmp exchangeTradeTmp = new ExchangeTradeTmp();
        exchangeTradeTmp.setIsDeleted(IsDeletedEnum.YES.getFlag());
        exchangeTradeTmpMapper.update(exchangeTradeTmp, new LambdaUpdateWrapper<ExchangeTradeTmp>().eq(ExchangeTradeTmp::getIsDeleted, IsDeletedEnum.NO));
        List<ExchangeAccountFeignVO> list = exchangeAccountClient.getList();
        for (ExchangeAccountFeignVO exchangeAccountFeignVO : list) {
            HashMap<String, String> map = new HashMap<>();
            map.put("ctpmsg", "{\"type\":103,\"data\":{\"UserID\":\"" + exchangeAccountFeignVO.getAccount() + "\"}}");
            stringRedisTemplate.opsForStream().add("ctp_mq", map);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean getExchangePosition() {
        //清空表中数据
        ExchangePositionTmp exchangePositionTmp = new ExchangePositionTmp();
        exchangePositionTmp.setIsDeleted(IsDeletedEnum.YES.getFlag());
        exchangePositionTmpMapper.update(exchangePositionTmp, new LambdaUpdateWrapper<ExchangePositionTmp>()
                .eq(ExchangePositionTmp::getIsDeleted, IsDeletedEnum.NO));

        List<ExchangeAccountFeignVO> list = exchangeAccountClient.getList();
        for (ExchangeAccountFeignVO exchangeAccountFeignVO : list) {
            HashMap<String, String> map = new HashMap<>();
            map.put("ctpmsg", "{\"type\":102,\"data\":{\"UserID\":\"" + exchangeAccountFeignVO.getAccount() + "\"}}");
            stringRedisTemplate.opsForStream().add("ctp_mq", map);
        }
        return Boolean.TRUE;
    }

    @Override
    public SettlementVO updateTradeObsDatePrice(LocalDate settlementDate) {
        RLock lock = redissonClient.getLock("lock:updateTradeObsDatePrice");
        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(0, 2, TimeUnit.MINUTES);
            if (tryLock) {
                log.info("开始处理观察价格");
                SettlementVO settlementVo = new SettlementVO();
                StringBuilder stringBuilder = new StringBuilder();
                //获取收盘价格
                Map<String, BigDecimal> lastDayTotalMarketMap = marketClient.getCloseMarketDataByDate(settlementDate);
                if (lastDayTotalMarketMap == null || lastDayTotalMarketMap.isEmpty()) {
                    BussinessException.E_300501.assertTrue(Boolean.FALSE);
                    settlementVo.setIsSuccess(Boolean.FALSE);
                    return settlementVo;
                }
                settlementVo.setIsSuccess(Boolean.TRUE);
                List<TradeObsDate> tradeObsDates = tradeObsDateMapper.selectList(new LambdaQueryWrapper<TradeObsDate>()
                        .eq(TradeObsDate::getObsDate, settlementDate)
                        .isNull(TradeObsDate::getPrice)
                        .eq(TradeObsDate::getIsDeleted, 0));
                stringBuilder.append("未生成观察价格的交易数量为:").append(tradeObsDates.size());

                for (TradeObsDate tradeObsDate : tradeObsDates) {
                    BigDecimal price = lastDayTotalMarketMap.get(tradeObsDate.getUnderlyingCode());
                    if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
                        log.error("合约代码{}的收盘价不存在", tradeObsDate.getUnderlyingCode());
                        BussinessException.E_300501.assertTrue(false, tradeObsDate.getUnderlyingCode());
                    }
                    tradeObsDate.setPrice(price);
                    ObsTradeDetail obsTradeDetail = this.cumulativeGenerateForward(tradeObsDate.getTradeId(), settlementDate, tradeObsDate.getPrice());
                    stringBuilder.append("\r\n").append(obsTradeDetail.getRemarks());
                    //只有未平仓的才更新观察价格和观察记录
                    if (!obsTradeDetail.getIsClose()) {
                        if (OptionTypeEnum.getNeedGenerateForwardOptionType().contains(obsTradeDetail.getOptionType())) {
                            obsTradeDetailMapper.insert(obsTradeDetail);
                        }
                        tradeObsDateMapper.updateById(tradeObsDate);
                    }

                }
                settlementVo.setMsg(stringBuilder.toString());
                return settlementVo;
            } else {
                BussinessException.E_300505.assertTrue(Boolean.FALSE);
                return new SettlementVO();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (tryLock && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("成功获取到数据，并释放锁");
            }
        }

    }

    @Override
    public ObsTradeDetail cumulativeGenerateForward(Integer tradeId, LocalDate tradeDate, BigDecimal closePrice) {
        ObsTradeDetail obsTradeDetail = new ObsTradeDetail();
        TradeMng tradeMng = tradeMngService.getById(tradeId);
        if (tradeMng == null) {
            obsTradeDetail.setIsClose(Boolean.FALSE);
            obsTradeDetail.setObsDate(tradeDate);
            obsTradeDetail.setRemarks("交易记录不存在！！！");
            return obsTradeDetail;
        }
        obsTradeDetail.setTradeCode(tradeMng.getTradeCode());
        obsTradeDetail.setObsDate(tradeDate);
        obsTradeDetail.setClientId(tradeMng.getClientId());
        obsTradeDetail.setOptionType(tradeMng.getOptionType());
        obsTradeDetail.setUnderlyingCode(tradeMng.getUnderlyingCode());
        //判断期权是否已平仓
        if (TradeStateEnum.getCloseStateList().contains(tradeMng.getTradeState())) {
            obsTradeDetail.setIsClose(Boolean.TRUE);
            obsTradeDetail.setRemarks("已平仓不生成远期");
            return obsTradeDetail;
        }
        obsTradeDetail.setIsClose(Boolean.FALSE);
        //结算方式
        SettleTypeEnum settleTypeEnum = tradeMng.getSettleType();

        if (!OptionTypeEnum.getNeedGenerateForwardOptionType().contains(tradeMng.getOptionType())) {
            return obsTradeDetail;
        }
        BigDecimal barrier = tradeMng.getBarrier();
        obsTradeDetail.setRemarks("收盘价:" + closePrice + ",障碍价格:" + barrier + ",执行价格:" + tradeMng.getStrike() + ";结算方式:" + settleTypeEnum.getDesc());
        TradeMng forward = new TradeMng();
        forward.setOptionType(OptionTypeEnum.AIForwardPricer);
        forward.setAssetId(tradeMng.getAssetId());
        forward.setClientId(tradeMng.getClientId());
        forward.setTradeDate(tradeDate);
        forward.setMaturityDate(getForwardMaturityDate(tradeMng.getUnderlyingCode(), tradeMng.getMaturityDate()));
        forward.setUnderlyingCode(tradeMng.getUnderlyingCode());
        forward.setSort(1);
        //远期的入场价格=累计期权的执行价格=远期的执行价格
        forward.setEntryPrice(tradeMng.getStrike());
        forward.setStrike(tradeMng.getStrike());
        //是否敲出
        Boolean isKnockOut = Boolean.FALSE;
        //是否需要生成远期
        Boolean isNeedForward = Boolean.TRUE;
        switch (tradeMng.getOptionType()) {
            case AICallAccPricer:
            case AIEnCallKOAccPricer:
            case AICallKOAccPricer:
            case AICallFixAccPricer:
            case AICallFixKOAccPricer:
               /*
                如果标的收盘价格小于执行价格，则生成客户买入远期，远期执行价格为累购执行价格，远期数量为每日数量*杠杆系数，
                如果标的收盘价大于或等于执行价格，小于障碍价格，则生成客户买入远期，远期执行价格为累购执行价格，远期数量为每日数量，
                如果标的收盘价大于或等于障碍价格，则不生成远期
                */
                if (barrier == null || barrier.compareTo(BigDecimal.ZERO) == 0) {
                    barrier = BigDecimal.valueOf(Double.MAX_VALUE);
                }
                if (closePrice.compareTo(tradeMng.getStrike()) < 0) {
                    forward.setTradeVolume(tradeMng.getBasicQuantity().multiply(tradeMng.getLeverage()));
                    // 熔断累计,到期敲入的远期数量=每日数量*到期倍数
                    if (tradeDate.isEqual(tradeMng.getMaturityDate()) && OptionTypeEnum.getKOOptionType().contains(tradeMng.getOptionType())) {
                        forward.setTradeVolume(tradeMng.getBasicQuantity().multiply(
                                tradeMng.getExpireMultiple().add(tradeMng.getLeverage()))
                        );
                    }
                } else if (closePrice.compareTo(barrier) < 0) {
                    //混合方式结算仅有杠杆时才生成远期；固定赔付不生成
                    if (settleTypeEnum == SettleTypeEnum.mix || OptionTypeEnum.getFixOptionType().contains(tradeMng.getOptionType())) {
                        isNeedForward = Boolean.FALSE;
                    }
                    forward.setTradeVolume(tradeMng.getBasicQuantity());
                }else if(tradeMng.getOptionType()==OptionTypeEnum.AIEnCallKOAccPricer){
                    //熔断增强敲出转远期规则: 入场价格、执行价格均为当天收盘价
                    forward.setEntryPrice(closePrice);
                    forward.setStrike(closePrice);
                    //远期数量为存续数量
                    forward.setTradeVolume(tradeMng.getAvailableVolume());
                    isKnockOut = Boolean.TRUE;
                } else {
                    isKnockOut = Boolean.TRUE;
                    isNeedForward = Boolean.FALSE;
                }
                forward.setBuyOrSell(tradeMng.getBuyOrSell());
                break;
            case AIPutAccPricer:
            case AIEnPutKOAccPricer:
            case AIPutKOAccPricer:
            case AIPutFixAccPricer:
            case AIPutFixKOAccPricer:
                /*
                 如果标的收盘价大于执行价格，则生成客户卖出远期，远期执行价格为累沽执行价格，远期数量为每日数量*杠杆系数，
                  如果标的收盘价小于或等于执行价格，大于障碍价格，则生成客户卖出远期，远期执行价格为累购执行价格，远期数量为每日数量，
                  如果标的收盘价小于或等于障碍价格，则不生成远期
                 */
                if (barrier == null || barrier.compareTo(BigDecimal.ZERO) == 0) {
                    barrier = BigDecimal.ZERO;
                }
                if (closePrice.compareTo(tradeMng.getStrike()) > 0) {
                    forward.setTradeVolume(tradeMng.getBasicQuantity().multiply(tradeMng.getLeverage()));
                    // 熔断累计,到期敲入的远期数量=每日数量*到期倍数
                    if (tradeDate.isEqual(tradeMng.getMaturityDate()) && OptionTypeEnum.getKOOptionType().contains(tradeMng.getOptionType())) {
                        forward.setTradeVolume(tradeMng.getBasicQuantity().multiply(tradeMng.getExpireMultiple().add(tradeMng.getLeverage())));
                    }
                } else if (closePrice.compareTo(barrier) > 0) {
                    //混合方式结算仅有杠杆时才生成远期；固定赔付不生成
                    if (settleTypeEnum == SettleTypeEnum.mix || OptionTypeEnum.getFixOptionType().contains(tradeMng.getOptionType())) {
                        isNeedForward = Boolean.FALSE;
                    }
                    forward.setTradeVolume(tradeMng.getBasicQuantity());
                } else if(tradeMng.getOptionType()==OptionTypeEnum.AIEnPutKOAccPricer){
                    //熔断增强敲出转远期规则: 入场价格、执行价格均为当天收盘价
                    forward.setEntryPrice(closePrice);
                    forward.setStrike(closePrice);
                    //远期数量为存续数量
                    forward.setTradeVolume(tradeMng.getAvailableVolume());
                    isKnockOut = Boolean.TRUE;
                }else {
                    isKnockOut = Boolean.TRUE;
                    isNeedForward = Boolean.FALSE;
                }
                //累沽方向相反
                if (tradeMng.getBuyOrSell() == BuyOrSellEnum.buy) {
                    forward.setBuyOrSell(BuyOrSellEnum.sell);
                } else {
                    forward.setBuyOrSell(BuyOrSellEnum.buy);
                }
                break;
            case AIEnAsianPricer:
                /*
                 * 对于增强亚式看涨期权，如果标的收盘价大于执行价格，则生成客户买入远期，远期执行价格为增强亚式执行价格，远期数量为总数量/观察次数
                 * 对于增强亚式看跌期权，如果标的收盘价小于执行价格，则生成客户卖出远期，远期执行价格为增强亚式执行价格，远期数量为总数量/观察次数
                 */

                if (closePrice.compareTo(tradeMng.getStrike()) > 0 && tradeMng.getCallOrPut() == CallOrPutEnum.call) {
                    forward.setTradeVolume(tradeMng.getTradeVolume()
                            .divide(BigDecimal.valueOf(tradeMng.getObsNumber()), 4, RoundingMode.HALF_UP));
                    forward.setBuyOrSell(tradeMng.getBuyOrSell());
                } else if (closePrice.compareTo(tradeMng.getStrike()) < 0 && tradeMng.getCallOrPut() == CallOrPutEnum.put) {
                    forward.setTradeVolume(tradeMng.getTradeVolume()
                            .divide(BigDecimal.valueOf(tradeMng.getObsNumber()), 4, RoundingMode.HALF_UP));
                    //看跌期权方向相反
                    if (tradeMng.getBuyOrSell() == BuyOrSellEnum.buy) {
                        forward.setBuyOrSell(BuyOrSellEnum.sell);
                    } else {
                        forward.setBuyOrSell(BuyOrSellEnum.buy);
                    }
                } else {
                    obsTradeDetail.setRemarks("增强亚式" + tradeMng.getCallOrPut().getDesc() + "->收盘价:" + closePrice + "执行价格:" + tradeMng.getStrike() + "不生成远期");
                    isNeedForward = Boolean.FALSE;
                }
                break;
            default:
                obsTradeDetail.setRemarks("交易编号:" + tradeMng.getTradeCode() + "错误的期权类型:" + tradeMng.getOptionType().getDesc());
                obsTradeDetail.setIsClose(Boolean.TRUE);
                isNeedForward = Boolean.FALSE;
        }


        //更新存续数量
        if (tradeMng.getOptionType() != OptionTypeEnum.AIEnAsianPricer &&
                tradeMng.getOptionType() != OptionTypeEnum.AIAsianPricer &&
                tradeMng.getOptionType() != OptionTypeEnum.AIBreakEvenSnowBallCallPricer &&
                tradeMng.getOptionType() != OptionTypeEnum.AIBreakEvenSnowBallPutPricer &&
                tradeMng.getOptionType() != OptionTypeEnum.AILimitLossesSnowBallCallPricer &&
                tradeMng.getOptionType() != OptionTypeEnum.AILimitLossesSnowBallPutPricer &&
                tradeMng.getOptionType() != OptionTypeEnum.AISnowBallCallPricer &&
                tradeMng.getOptionType() != OptionTypeEnum.AISnowBallPutPricer) {
            //敲出情景下，仅有普通累计期权需要更新存续数量
            if (OptionTypeEnum.getKOOptionType().contains(tradeMng.getOptionType())) {
                if (!isKnockOut){
                    tradeMng.setAvailableVolume(tradeMng.getAvailableVolume().subtract(tradeMng.getBasicQuantity()));
                    tradeMng.setAvailableNotionalPrincipal(tradeMng.getAvailableVolume().multiply(tradeMng.getEntryPrice()));
                    tradeMngService.updateById(tradeMng);
                }
            }else {
                tradeMng.setAvailableVolume(tradeMng.getAvailableVolume().subtract(tradeMng.getBasicQuantity()));
                tradeMng.setAvailableNotionalPrincipal(tradeMng.getAvailableVolume().multiply(tradeMng.getEntryPrice()));
                tradeMngService.updateById(tradeMng);
            }
        }
        if (settleTypeEnum == SettleTypeEnum.cash) {
            obsTradeDetail.setRemarks("现金结算不生成远期");
            return obsTradeDetail;
        }
        if (!isNeedForward) {
            return obsTradeDetail;
        }
        if (forward.getTradeVolume().compareTo(BigDecimal.ZERO) == 0) {
            obsTradeDetail.setRemarks("生成的远期交易数量为0,无效交易");
            return obsTradeDetail;
        }
        //定价计算
        QuoteCalculateDTO quoteDto = new QuoteCalculateDTO();
        quoteDto.setTradeType(TradeTypeEnum.singleLeg);
        quoteDto.setOpenOrClose(OpenOrCloseEnum.open);
        List<QuoteCalculateDetailDTO> quoteCalculateDetailDTOList = new ArrayList<>();
        QuoteCalculateDetailDTO detailDto = CglibUtil.copy(forward, QuoteCalculateDetailDTO.class);
        if (detailDto.getEntryPrice().compareTo(BigDecimal.ZERO) == 0) {
            log.error("{}远期价格为0", tradeMng.getTradeCode());
            detailDto.setEntryPrice(BigDecimal.ONE);
        }
        quoteCalculateDetailDTOList.add(detailDto);
        quoteDto.setQuoteList(quoteCalculateDetailDTOList);
        QuoteStringResultVo resultVo = quoteService.quote(quoteDto).get(0);
        forward.setPv(new BigDecimal(resultVo.getPv()));
        forward.setDelta(new BigDecimal(resultVo.getDelta()));
        forward.setGamma(new BigDecimal(resultVo.getGamma()));
        forward.setVega(new BigDecimal(resultVo.getVega()));
        forward.setTheta(new BigDecimal(resultVo.getTheta()));
        forward.setRho(new BigDecimal(resultVo.getRho()));
        forward.setOptionPremium(BigDecimal.ZERO);
        forward.setOptionPremiumPercent(BigDecimal.ZERO);
        forward.setDay1PnL(new BigDecimal(resultVo.getDay1PnL()));
        forward.setMargin(new BigDecimal(resultVo.getMargin()));
        forward.setTotalAmount(new BigDecimal(resultVo.getTotalAmount()));
        //设置日期信息
        CalendarPropertyQueryDto calendarPropertyQueryDto = new CalendarPropertyQueryDto();
        calendarPropertyQueryDto.setTradeDate(forward.getTradeDate());
        calendarPropertyQueryDto.setMaturityDate(forward.getMaturityDate());
        CalendarProperty calendarProperty = calendarClient.getCalendarProperty(calendarPropertyQueryDto);
        forward.setTtm(calendarProperty.getTtm());
        forward.setWorkday(calendarProperty.getWorkday());
        forward.setTradingDay(calendarProperty.getTradingDay());
        forward.setBankHoliday(calendarProperty.getBankHoliday());
        //关联交易编号
        forward.setRelevanceTradeCode(tradeMng.getTradeCode());
        TradeInsertDTO dto = new TradeInsertDTO();
        dto.setTradeList(Collections.singletonList(CglibUtil.copy(forward, TradeMngDTO.class)));
        dto.setTradeType(TradeTypeEnum.singleLeg);
        dto.setTraderId(tradeMng.getTraderId());
        dto.setAssetId(tradeMng.getAssetId());
        List<TradeMngVO> tradeMngList = tradeMngService.insertTrade(dto);
        log.debug("交易记录ID:{},于{}生成远期信息为{}", tradeId, tradeDate, JSONObject.toJSONString(tradeMngList));

        obsTradeDetail.setForwardTradeCode(tradeMngList.get(0).getTradeCode());
        return obsTradeDetail;
    }

    /**
     * 获取远期的到期日
     * @param underlyingCode 合约代码
     * @param maturityDate   累计期权的到期日
     * @return 远期到期日
     */
    private LocalDate getForwardMaturityDate(String underlyingCode, LocalDate maturityDate) {
        TradayAddDaysDto tradayAddDaysDto = new TradayAddDaysDto();
        tradayAddDaysDto.setDate(maturityDate);
        tradayAddDaysDto.setDays(15);
        //交易到期日期往后退取15个交易日
        LocalDate tempDate = calendarClient.tradeDayAddDays(tradayAddDaysDto);
        UnderlyingManagerVO underlyingManagerVo = underlyingManagerClient.getUnderlyingByCode(underlyingCode);
        //合约到期日前一个月的25号 E-25
        LocalDate expireDate = underlyingManagerVo.getExpireDate().minusMonths(1).withDayOfMonth(20);
        Boolean isTradeDay = calendarClient.isTradeDay(expireDate);
        if (!isTradeDay) {
            tradayAddDaysDto.setDate(expireDate);
            tradayAddDaysDto.setDays(-1);
            expireDate = calendarClient.tradeDayAddDays(tradayAddDaysDto);
        }
        //取M+15与E-25的最大日期
        expireDate = tempDate.isBefore(expireDate) ? tempDate : expireDate;
        //取完最大日期后 与合约的到期日取较小的日期
        if (expireDate.isAfter(maturityDate)) {
            return expireDate;
        } else {
            return maturityDate;
        }
    }

    @Override
    public SettlementVO saveTradeRiskInfo(SettlementDTO settlementDto) {
        SettlementVO settlementVo = new SettlementVO();
        StopWatch stopWatch = StopWatch.create("收盘计算风险数据");
        stopWatch.start("初始化数据");
        //获取系统配置信息
        riskCacular.initSystemConfig();
        //今日开平仓
        Map<OpenOrCloseEnum, Map<String, BigDecimal>> result=  riskService.getOpenAndClose(riskCacular.getTradeDay());
        riskCacular.setTodayOpenTradeAmountMap(result.getOrDefault(OpenOrCloseEnum.open,new HashMap<>()));
        riskCacular.setTodayCloseTradeAmountMap(result.getOrDefault(OpenOrCloseEnum.close,new HashMap<>()));
        //昨日盈亏
        List<TradeRiskInfo> lastRiskList = tradeRiskInfoService.getTradeTotalPnl(riskCacular.getTradeDay(),true, false);
        if (lastRiskList != null) {
            riskCacular.setLastRiskInfoMap(lastRiskList.stream().collect(
                    Collectors.toMap(TradeRiskInfo::getId, tradeRiskInfo -> CglibUtil.copy(tradeRiskInfo, TradeRiskCacularResult.class))));
        } else {
            riskCacular.setLastRiskInfoMap(new HashMap<>());
        }
        stopWatch.stop();

        stopWatch.start("获取场内数据");
        List<ExchangeRealTimePos> exchangeRealTimePosList = riskCacular.getExchangeRiskList();
        Map<String, List<ExchangeRealTimePos>> exchangeRealTimePosMap = exchangeRealTimePosList.stream().collect(Collectors.groupingBy(e -> e.getUnderlyingCode().toUpperCase()));
        stopWatch.stop();

        stopWatch.start("获取场外数据");
        //存续大于0的交易记录
        List<TradeMngVO> survivalTrade = tradeMngService.getSurvivalTradeByTradeDay(riskCacular.getTradeDay());

        //当天平仓的交易记录
        List<TradeMngVO> todayCloseTrade = tradeMngService.getCloseTradeByTradeDay(riskCacular.getTradeDay());
        survivalTrade.addAll(todayCloseTrade);
        List<TradeMngVO> list = CglibUtil.copyList(survivalTrade, TradeMngVO::new, (s, t) -> {
            if (s.getTradeObsDateList() != null) {
                t.setTradeObsDateList(CglibUtil.copyList(s.getTradeObsDateList(), TradeObsDateVO::new));
            }
        });
        List<TradeRiskInfo> dbList = new ArrayList<>();
        Map<String, BigDecimal> closePriceMap = marketClient.getCloseMarketDataByDate(settlementDto.getSettlementDate());

        stopWatch.stop();
        stopWatch.start("计算保证金");

        Map<String, BigDecimal> marinMap = getTradeMargin(list, settlementDto.getSettlementDate());
        stopWatch.stop();
        stopWatch.start("数据计算");
        Set<String> underlyingSet = new HashSet<>();
        Map<String, List<TradeMngVO>> octListMap = survivalTrade.stream().collect(Collectors.groupingBy(TradeMngVO::getUnderlyingCode));
        underlyingSet.addAll(octListMap.keySet());
        underlyingSet.addAll(exchangeRealTimePosMap.keySet());

        for (String underlyingCode : underlyingSet) {
            UnderlyingByRisk underlyingByRisk = riskCacular.getUnderlyingByRisk(underlyingCode);
            //设置波动率
            underlyingByRisk.setMidVolatility(volatilityService.getVolatilityByTypeAndDate(underlyingCode,settlementDto.getSettlementDate(), VolTypeEnum.mid));
            //设置收盘价格
            underlyingByRisk.setLastPrice(closePriceMap.get(underlyingCode));
            underlyingByRisk.setEvaluationTime(settlementDto.getSettlementDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
            List<TradeMngVO> tradeMngVOList = octListMap.get(underlyingCode);
            if (tradeMngVOList != null && !tradeMngVOList.isEmpty()) {
                tradeMngVOList.forEach(tradeMngVo -> {
                    //场外计算
                    TradeRiskCacularResult tradeRiskCacularResult = riskCacular.overCacularRisk(tradeMngVo, underlyingByRisk);
                    TradeRiskInfo tradeRiskInfo = CglibUtil.copy(tradeRiskCacularResult, TradeRiskInfo.class);
                    tradeRiskInfo.setMargin(marinMap.get(tradeRiskInfo.getTradeCode()));
                    dbList.add(tradeRiskInfo);

                });
            }
            getExchangeRiskResult(exchangeRealTimePosMap, dbList, underlyingByRisk);
        }
        stopWatch.stop();
        stopWatch.start("风险数据落库");
        tradeRiskInfoService.saveTradeRiskInfoBatch(settlementDto, dbList);
        stopWatch.stop();
        log.info("保存风险耗时:{}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        settlementVo.setMsg("风险数据快照保存成功");
        settlementVo.setIsSuccess(Boolean.TRUE);
        return settlementVo;
    }

    @Override
    public Map<String, BigDecimal> getTradeMargin(List<TradeMngVO> list, LocalDate settlementDate) {
        //过滤掉存续数量为0的交易
        Map<Integer, List<TradeMngVO>> clientTradeMap = list.stream().filter(a->a.getAvailableVolume().compareTo(BigDecimal.ZERO)>0
                && a.getOptionType()!=OptionTypeEnum.AICustomPricer)
                .collect(Collectors.groupingBy(TradeMngVO::getClientId));
        List<MarginQuoteDTO.MarginDTO> marginDTOArrayList = new ArrayList<>();
        Map<Integer,BigDecimal> clientMarginRateMap= clientClient.getClientMarginRate(new HashSet<>());
        Map<String, BigDecimal> closePriceMap = marketClient.getCloseMarketDataByDate(settlementDate);
        Map<String, BigDecimal> settlementMap = marketClient.getSettlementMarketDataByDate(settlementDate);
        Set<String> underlyingCodeSet = list.stream().map(TradeMngVO::getUnderlyingCode).collect(Collectors.toSet());
        List<Volatility> volatilityList = volatilityService.getNewVolatility(underlyingCodeSet, settlementDate);
        Map<String, List<VolatityDataDto>> volMap = volatilityList.stream().filter(v -> v.getVolType() == VolTypeEnum.mid)
                .collect(Collectors.toMap(Volatility::getUnderlyingCode, Volatility::getData));

        List<UnderlyingManagerVO> underlyingManagerVOList = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet);
        //查询合约股息率
        BigDecimal systemDividendYield = BigDecimalUtil.percentageToBigDecimal(systemConfigUtil.getDividendYield());
        for (UnderlyingManagerVO underlying : underlyingManagerVOList) {
            if (Objects.nonNull(underlying.getDividendYield())) {
                underlying.setDividendYield(BigDecimalUtil.percentageToBigDecimal(underlying.getDividendYield()));
            } else {
                underlying.setDividendYield(systemDividendYield);
            }
        }
        Map<String, UnderlyingManagerVO> underlyingManagerVOMap = underlyingManagerVOList.stream().collect(Collectors.toMap(UnderlyingManagerVO::getUnderlyingCode, Function.identity()));

        for (Map.Entry<Integer, List<TradeMngVO>> entry : clientTradeMap.entrySet()) {
            MarginQuoteDTO.MarginDTO marginDTO = new MarginQuoteDTO.MarginDTO();
            //组装保证金计算数据
            marginDTO.setTradeData(getMarinTradeData(entry.getValue(),closePriceMap,settlementMap,volMap,underlyingManagerVOMap, settlementDate));
            marginDTO.setClientId(entry.getKey());
            marginDTO.setMarginRate(clientMarginRateMap.get(entry.getKey()));
            marginDTOArrayList.add(marginDTO);
        }
        MarginQuoteDTO marginQuoteDTO = new MarginQuoteDTO();
        marginQuoteDTO.setData(marginDTOArrayList);
        log.info("计算客户保证金参数:{}",JSONObject.toJSONString(marginQuoteDTO));
        PythonResult<MarginVO> marinVOPythonResult = pythonClient.margin(marginQuoteDTO);
        BussinessException.E_300502.assertTrue(marinVOPythonResult.getError_message().isEmpty(), JSONObject.toJSONString(marinVOPythonResult.getError_message()));
        log.info("计算客户保证金响应:{}",JSONObject.toJSONString(marinVOPythonResult));
        List<MarginVO> marginVOList = marinVOPythonResult.getResults();
        return marginVOList.stream().collect(Collectors.toMap(MarginVO::getTradeCode, MarginVO::getMargin, (v1, v2) -> v2));
    }

    @Override
    public Map<String, BigDecimal> getTradeNowMargin(List<TradeMngVO> list) {
        //过滤掉存续数量为0与自定义期权的交易
        Map<Integer, List<TradeMngVO>> clientTradeMap = list.stream().filter(a->a.getAvailableVolume().compareTo(BigDecimal.ZERO)>0
                && a.getOptionType()!=OptionTypeEnum.AICustomPricer)
                .collect(Collectors.groupingBy(TradeMngVO::getClientId));

        Map<Integer,BigDecimal> clientMarginRateMap= clientClient.getClientMarginRate(new HashSet<>());
        Set<String> underlyingCodeSet = list.stream().map(TradeMngVO::getUnderlyingCode).collect(Collectors.toSet());
        Map<String, BigDecimal> lastPriceMap = marketClient.getLastPriceByUnderlyingCodeList(new HashSet<>(underlyingCodeSet));
        LocalDate settlementDate = systemConfigUtil.getTradeDay();
        List<Volatility> volatilityList = volatilityService.getNewVolatility(underlyingCodeSet, settlementDate);
        Map<String, List<VolatityDataDto>> volMap = volatilityList.stream().filter(v -> v.getVolType() == VolTypeEnum.mid)
                .collect(Collectors.toMap(Volatility::getUnderlyingCode, Volatility::getData));
        List<UnderlyingManagerVO> underlyingManagerVOList = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet);
        //查询合约股息率
        BigDecimal systemDividendYield = BigDecimalUtil.percentageToBigDecimal(systemConfigUtil.getDividendYield());
        for (UnderlyingManagerVO underlying : underlyingManagerVOList) {
            if (Objects.nonNull(underlying.getDividendYield())) {
                underlying.setDividendYield(BigDecimalUtil.percentageToBigDecimal(underlying.getDividendYield()));
            } else {
                underlying.setDividendYield(systemDividendYield);
            }
        }
        Map<String, UnderlyingManagerVO> underlyingManagerVOMap = underlyingManagerVOList.stream().collect(Collectors.toMap(UnderlyingManagerVO::getUnderlyingCode, Function.identity()));
        List<MarginQuoteDTO.MarginDTO> marginDTOArrayList = new ArrayList<>();
        for (Map.Entry<Integer, List<TradeMngVO>> entry : clientTradeMap.entrySet()) {
            MarginQuoteDTO.MarginDTO marginDTO = new MarginQuoteDTO.MarginDTO();
            marginDTO.setTradeData(getMarinTradeData(entry.getValue(),lastPriceMap, lastPriceMap,volMap,underlyingManagerVOMap, settlementDate));
            marginDTO.setClientId(entry.getKey());
            marginDTO.setMarginRate(clientMarginRateMap.get(entry.getKey()));
            marginDTOArrayList.add(marginDTO);
        }
        MarginQuoteDTO marginQuoteDTO = new MarginQuoteDTO();
        marginQuoteDTO.setData(marginDTOArrayList);
        log.info("计算客户保证金参数:{}", JSONObject.toJSONString(marginQuoteDTO));
        PythonResult<MarginVO> marinVOPythonResult = pythonClient.margin(marginQuoteDTO);
        BussinessException.E_300502.assertTrue(marinVOPythonResult.getError_message().isEmpty(), JSONObject.toJSONString(marinVOPythonResult.getError_message()));
        log.info("计算客户保证金响应:{}", JSONObject.toJSONString(marinVOPythonResult));
        return marinVOPythonResult.getResults().stream().collect(Collectors.toMap(MarginVO::getTradeCode, MarginVO::getMargin, (v1, v2) -> v2));
    }

    /**
     * 组装保证金计算数据
     * @param value 交易记录
     * @param closePriceMap 收盘价
     * @param settlementMap 结算价
     * @param volMap 波动率
     * @param underlyingManagerVOMap 合约信息
     * @param settlementDate 计算日期
     * @return 计算参数
     */
    private List<MarinTradeDataDTO> getMarinTradeData(List<TradeMngVO> value, Map<String, BigDecimal> closePriceMap
            , Map<String, BigDecimal> settlementMap, Map<String, List<VolatityDataDto>> volMap
            , Map<String, UnderlyingManagerVO> underlyingManagerVOMap, LocalDate settlementDate) {
        return CglibUtil.copyList(value, MarinTradeDataDTO::new, (source, target) -> {

            target.setOptionType(source.getOptionType().getType());
            target.setVarietyCode(underlyingManagerVOMap.get(source.getUnderlyingCode()).getVarietyCode());
            target.setUpDownLimit(BigDecimalUtil.percentageToBigDecimal(underlyingManagerVOMap.get(source.getUnderlyingCode()).getUpDownLimit()));
            target.setSettlementTime(settlementDate.atTime(15, 0));
            target.setMaturityDate(source.getMaturityDate().atTime(15, 0));
            if (source.getOptionType().name().toLowerCase().contains(CallOrPutEnum.call.name())) {
                source.setCallOrPut(CallOrPutEnum.call);
            }
            if (source.getOptionType().name().toLowerCase().contains(CallOrPutEnum.put.name())) {
                source.setCallOrPut(CallOrPutEnum.put);
            }
            if (source.getCallOrPut()!=null){
                target.setCallOrPut(source.getCallOrPut().name());
            }
            //客户方向
            target.setBuyOrSell(source.getBuyOrSell() == BuyOrSellEnum.buy ? 1 : -1);
            target.setRiskFreeInterestRate(BigDecimalUtil.percentageToBigDecimal(systemConfigUtil.getRiskFreeInterestRate()));
            target.setDividendYield(underlyingManagerVOMap.get(source.getUnderlyingCode()).getDividendYield());
            target.setClosePrice(closePriceMap.getOrDefault(source.getUnderlyingCode(), BigDecimal.ZERO));
            //取不到结算价时使用收盘价
            target.setSettlementPrice(settlementMap.getOrDefault(source.getUnderlyingCode(), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)==0?
                    closePriceMap.getOrDefault(source.getUnderlyingCode(), BigDecimal.ZERO):settlementMap.get(source.getUnderlyingCode()));
            target.setVolatityList(CglibUtil.copyList(volMap.get(source.getUnderlyingCode()), VolatityDataDTO::new));
            target.setConstVol(BigDecimalUtil.percentageToBigDecimal(source.getRiskVol() == null ? BigDecimal.ZERO : source.getRiskVol()));
            target.setExpiryLeverage(source.getExpireMultiple()== null ? BigDecimal.ZERO :source.getExpireMultiple());
            target.setAccumulatorType(QuoteUtil.getAccumulatorType(source.getOptionType()));
            target.setStrikeRamp(source.getStrikeRamp()==null? BigDecimal.ZERO :source.getStrikeRamp());
            target.setBarrierRamp(source.getBarrierRamp()==null? BigDecimal.ZERO :source.getBarrierRamp());
            //结算方式
            if (source.getSettleType() != null) {
                target.setSettleType(source.getSettleType().getKey());
            }
            target.setKnockoutRebate(source.getKnockoutRebate()==null?BigDecimal.ZERO:source.getKnockoutRebate());
            //观察日列表
            if (source.getTradeObsDateList() != null && !source.getTradeObsDateList().isEmpty()) {
                target.setObsDateList(CglibUtil.copyList(source.getTradeObsDateList(), TradeObsDateDTO::new, (vo, dto) -> {
                    dto.setObsDate(vo.getObsDate().atTime(15, 0, 0));
                    if (vo.getPrice() == null) {
                        dto.setPrice(BigDecimal.ZERO);
                    }
                    //雪球期权的敲出价格
                    if (vo.getBarrierRelative() != null && vo.getBarrierRelative()) {
                        dto.setCallBarrier(BigDecimalUtil.percentageToBigDecimal(vo.getBarrier()).multiply(source.getEntryPrice()));
                        dto.setCallBarriershift(BigDecimalUtil.percentageToBigDecimal(vo.getBarrierShift()).multiply(source.getEntryPrice()));
                    } else {
                        dto.setCallBarrier(vo.getBarrier());
                        dto.setCallBarriershift(vo.getBarrierShift());
                    }
                    //敲出票息
                    if (vo.getRebateRate() != null) {
                        dto.setCoupon(BigDecimalUtil.percentageToBigDecimal(vo.getRebateRate()));
                    }
                    dto.setIsCouponannualized(vo.getRebateRateAnnulized());

                }));
            }
            //source.getVarietyName()


        });
    }


    /**
     * 获取场内计算结果
     * @param exchangeRealTimePosMap 计算数据
     * @param dbList                 结算结果
     */
    private void getExchangeRiskResult(Map<String, List<ExchangeRealTimePos>> exchangeRealTimePosMap, List<TradeRiskInfo> dbList, UnderlyingByRisk underlyingByRisk) {
        List<ExchangeRealTimePos> realTimePosList = exchangeRealTimePosMap.get(underlyingByRisk.getUnderlyingCode());
        if (realTimePosList != null && !realTimePosList.isEmpty()) {
            realTimePosList.forEach(exchangeRealTimePos -> {
                //场内计算
                TradeRiskCacularResult tradeRiskCacularResult = riskCacular.exchangeCacularRisk(exchangeRealTimePos, underlyingByRisk);
                dbList.add(CglibUtil.copy(tradeRiskCacularResult, TradeRiskInfo.class));
            });
        }
    }

    @Override
    public SettlementVO saveExchangeTradeRiskInfo(SettlementDTO settlementDto) {
        SettlementVO settlementVo = new SettlementVO();
        //获取系统配置信息
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisAdapter.SYSTEM_CONFIG_INFO);
        Map<String, String> systemInfoMap = entries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
        //系统交易日
        riskCacular.setTradeDay(settlementDto.getSettlementDate());
        TradayAddDaysDto tradayAddDaysDto = new TradayAddDaysDto();
        tradayAddDaysDto.setDays(-1);
        tradayAddDaysDto.setDate(settlementDto.getSettlementDate());
        //上一个交易日
        riskCacular.setLastTradeDay(calendarClient.tradeDayAddDays(tradayAddDaysDto));
        //股息率
        riskCacular.setDividendYield(BigDecimalUtil.percentageToBigDecimal(new BigDecimal(systemInfoMap.get(SystemConfigEnum.dividendYield.name()))));
        //无风险利率
        riskCacular.setRiskFreeInterestRate(BigDecimalUtil.percentageToBigDecimal(new BigDecimal(systemInfoMap.get(SystemConfigEnum.riskFreeInterestRate.name()))));
        //今日开平仓
        Map<OpenOrCloseEnum, Map<String, BigDecimal>> result=  riskService.getOpenAndClose(riskCacular.getTradeDay());
        riskCacular.setTodayOpenTradeAmountMap(result.getOrDefault(OpenOrCloseEnum.open,new HashMap<>()));
        riskCacular.setTodayCloseTradeAmountMap(result.getOrDefault(OpenOrCloseEnum.close,new HashMap<>()));
        //昨日盈亏
        List<TradeRiskInfo> lastRiskList = tradeRiskInfoService.getTradeTotalPnl(riskCacular.getTradeDay(),true, false);
        if (lastRiskList != null) {
            riskCacular.setLastRiskInfoMap(lastRiskList.stream().collect(Collectors.toMap(TradeRiskInfo::getId, tradeRiskInfo -> CglibUtil.copy(tradeRiskInfo, TradeRiskCacularResult.class))));
        } else {
            riskCacular.setLastRiskInfoMap(new HashMap<>());
        }
        //对应补单的场内持仓信息
        List<ExchangeRealTimePos> exchangeRealTimePosList = exchangePositionService.selectPositionBySupplementaryAndTradingDay(settlementDto.getSettlementDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Map<String, List<ExchangeRealTimePos>> exchangeRealTimePosMap = exchangeRealTimePosList.stream().collect(Collectors.groupingBy(e -> e.getUnderlyingCode().toUpperCase()));

        List<TradeRiskInfo> dbList = new ArrayList<>();
        Map<String, BigDecimal> closePriceMap = marketClient.getCloseMarketDataByDate(settlementDto.getSettlementDate());
        Set<String> underlyingSet = new HashSet<>(exchangeRealTimePosMap.keySet());
        for (String underlyingCode : underlyingSet) {
            UnderlyingByRisk underlyingByRisk = riskCacular.getUnderlyingByRisk(underlyingCode);
            //设置波动率
            underlyingByRisk.setMidVolatility(volatilityService.getVolatilityByTypeAndDate(underlyingCode,settlementDto.getSettlementDate(), VolTypeEnum.mid));
            //设置收盘价格
            underlyingByRisk.setLastPrice(closePriceMap.get(underlyingCode));
            underlyingByRisk.setEvaluationTime(settlementDto.getSettlementDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());

            getExchangeRiskResult(exchangeRealTimePosMap, dbList, underlyingByRisk);
        }
        tradeRiskInfoService.saveTradeRiskInfoBatch(settlementDto, dbList);
        //补单缓存刷新
        stringRedisTemplate.delete(RedisAdapter.TRADE_LAST_RISK_INFO + riskCacular.getTradeDay());
        settlementVo.setMsg("计算场内持仓成功");
        settlementVo.setIsSuccess(Boolean.TRUE);
        return settlementVo;
    }

    @Override
    public Boolean checkTodayCaclPos() {
        String today = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //校验交易记录
        Long exchangeTradeTmpCount = exchangeTradeTmpMapper.selectCount(new LambdaQueryWrapper<ExchangeTradeTmp>().
                eq(ExchangeTradeTmp::getTradingDay, today)
                .eq(ExchangeTradeTmp::getIsDeleted, IsDeletedEnum.NO));

        Long exchangeTradeCount = exchangeTradeMapper.selectCount(new LambdaQueryWrapper<ExchangeTrade>().
                eq(ExchangeTrade::getTradingDay, today)
                .eq(ExchangeTrade::getIsDeleted, IsDeletedEnum.NO));
        if (!exchangeTradeTmpCount.equals(exchangeTradeCount)) {
            //将获取到场内的交易记录同步到exchangeTrade表中
            riskService.fromTmpToExchangeTrade();
            //重新计算持仓
            riskService.reCalculationPos();
            //校验计算的持仓
            riskService.checkExchangePos();
        } else {
            riskService.checkExchangePos();
        }
        riskService.fromTmpToExchangePosition();
        return Boolean.TRUE;
    }

    @Override
    public SettlementVO getCheckTodayPosResult() {

        String today = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        SettlementVO settlementVo = new SettlementVO();
        //获取校验计算的持仓的结果
        LambdaQueryWrapper<ExchangePositionCheck> lambdaQueryWrapper = new LambdaQueryWrapper<ExchangePositionCheck>()
                .eq(ExchangePositionCheck::getTradingDay, today)
                .eq(ExchangePositionCheck::getStatus, SuccessStatusEnum.faild)
                .eq(ExchangePositionCheck::getIsDeleted, 0);
        List<ExchangePositionCheck> exchangePositionChecks = exchangePositionCheckMapper.selectList(lambdaQueryWrapper);
        if (!exchangePositionChecks.isEmpty()) {
            settlementVo.setIsSuccess(Boolean.FALSE);
            StringBuilder stringBuilder = new StringBuilder();
            for (ExchangePositionCheck exchangePositionCheck : exchangePositionChecks) {
                stringBuilder.append(exchangePositionCheck.getInvestorId()).append(" ").append(exchangePositionCheck.getInstrumentId()).append(" ").append(exchangePositionCheck.getPosiDirection() == 2 ? "多头" : " 空头").append(";");
            }
            settlementVo.setMsg(stringBuilder.toString());
        } else {
            settlementVo.setIsSuccess(Boolean.TRUE);
            settlementVo.setMsg("成功");
        }
        return settlementVo;
    }

    @Override
    public Boolean checkObsStatus(LocalDate settlementDate) {
        //获取存续的需要观察的累计期权ID
        List<Integer> tradeIdList = tradeMngService.list(new LambdaQueryWrapper<TradeMng>()
                        .select(TradeMng::getId)
                        .eq(TradeMng::getIsDeleted, IsDeletedEnum.NO)
                        .in(TradeMng::getTradeState, TradeStateEnum.getLiveStateList())
                        .in(TradeMng::getOptionType, OptionTypeEnum.getHaveObsType()))
                .stream().map(TradeMng::getId).collect(Collectors.toList());
        //检查是否存在未观察价格的累计期权
        Long tradeObsDates = tradeObsDateMapper.selectCount(new LambdaQueryWrapper<TradeObsDate>()
                .eq(TradeObsDate::getObsDate, settlementDate)
                .isNull(TradeObsDate::getPrice)
                .in(TradeObsDate::getTradeId, tradeIdList)
                .eq(TradeObsDate::getIsDeleted, 0));
        return tradeObsDates == 0;
    }

    @Override
    public SettlementVO updateTodayPosData(SettlementDTO settlementDto) {
        String date = settlementDto.getSettlementDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return riskService.updatePosData(date);

    }

    @Override
    public SettlementVO copyPosDataToNextTradeDay() {
        SettlementVO settlementVo = new SettlementVO();
        if (riskService.copyPosDataToNextTradeDay()) {
            settlementVo.setIsSuccess(Boolean.TRUE);
            settlementVo.setMsg("初始化持仓成功");
        } else {
            settlementVo.setIsSuccess(Boolean.FALSE);
            settlementVo.setMsg("初始化持仓失败");
        }

        return settlementVo;
    }

    @Override
    public SettlementVO updateKnockedIn(SettlementDTO settlementDto) {
        SettlementVO settlementVo = new SettlementVO();
        settlementVo.setIsSuccess(true);
        settlementVo.setMsg("执行成功");
        StringBuilder stringBuilder = new StringBuilder();
        LambdaQueryWrapper<TradeMng> tradeMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeMngLambdaQueryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        tradeMngLambdaQueryWrapper.in(TradeMng::getOptionType, Arrays.asList(
                OptionTypeEnum.AISnowBallCallPricer,
                OptionTypeEnum.AISnowBallPutPricer,
                OptionTypeEnum.AILimitLossesSnowBallCallPricer,
                OptionTypeEnum.AILimitLossesSnowBallPutPricer)); // 期权类型为雪球的
        // 状态存活的
        tradeMngLambdaQueryWrapper.in(TradeMng::getTradeState, TradeStateEnum.getLiveStateList());
        List<TradeMng> tradeMngList = tradeMngMapper.selectList(tradeMngLambdaQueryWrapper);
        if (tradeMngList != null && !tradeMngList.isEmpty()) {
            Set<Integer> tradeIds = tradeMngList.stream().map(TradeMng::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<TradeSnowballOption> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(TradeSnowballOption::getTradeId, tradeIds);
            lambdaQueryWrapper.eq(TradeSnowballOption::getIsDeleted, IsDeletedEnum.NO);
            lambdaQueryWrapper.eq(TradeSnowballOption::getAlreadyKnockedIn, false);
            List<TradeSnowballOption> list = tradeSnowballOptionMapper.selectList(lambdaQueryWrapper);
            for (TradeSnowballOption item : list) {
                TradeMng tradeMng = tradeMngList.stream().filter(mng -> mng.getId().equals(item.getTradeId())).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300100));
                boolean flag;
                BigDecimal closePrice;
                Map<String, BigDecimal> lastDayTotalMarketMap = marketClient.getCloseMarketDataByDate(settlementDto.getSettlementDate());
                closePrice = lastDayTotalMarketMap.get(tradeMng.getUnderlyingCode());
                BigDecimal knockinBarrierValue = item.getKnockinBarrierValue();
                if (item.getKnockinBarrierRelative()) { // 相对
                    knockinBarrierValue = tradeMng.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getKnockinBarrierValue()));
                }
                if (tradeMng.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer) { // 看涨
                    flag = closePrice.compareTo(knockinBarrierValue) < 0;
                } else { // 看跌
                    flag = closePrice.compareTo(knockinBarrierValue) > 0;
                }
                if (flag) {
                    String msg = "更新敲入标记的交易编号=";
                    if (!stringBuilder.toString().contains(msg)) {
                        stringBuilder.append("\r\n").append(msg).append(tradeMng.getTradeCode());
                    } else {
                        stringBuilder.append("\r\n").append(tradeMng.getTradeCode());
                    }
                    SnowKnockedinLog snowKnockedinLog = new SnowKnockedinLog();
                    snowKnockedinLog.setKnockedInDate(LocalDate.now());
                    snowKnockedinLog.setUnderlyingCode(tradeMng.getUnderlyingCode());
                    snowKnockedinLog.setTradeCode(tradeMng.getTradeCode());
                    snowKnockedinLog.setOptionType(tradeMng.getOptionType());
                    snowKnockedinLog.setReamrks(stringBuilder.toString());
                    snowKnockedinLog.setKnockinBarrierValue(item.getKnockinBarrierValue());
                    snowKnockedinLog.setClosePrice(closePrice);
                    snowKnockedinLogMapper.insert(snowKnockedinLog);
                    LambdaUpdateWrapper<TradeSnowballOption> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(TradeSnowballOption::getAlreadyKnockedIn, true);
                    updateWrapper.eq(TradeSnowballOption::getId, item.getId());
                    int count = tradeSnowballOptionMapper.update(null, updateWrapper);
                    if (count <= 0) {
                        settlementVo.setIsSuccess(false);
                        stringBuilder.append("\r\n").append("更新失败");
                    }
                }
            }
        } else {
            settlementVo.setIsSuccess(true);
            settlementVo.setMsg("暂无待更新数据");
        }
        return settlementVo;
    }

    @Override
    public SettlementVO saveCloseTradeTotalPnl() {
        SettlementVO settlementVo = new SettlementVO();
        settlementVo.setIsSuccess(Boolean.TRUE);
        tradeRiskInfoService.saveCloseTradeTotalPnl();
        settlementVo.setMsg("保存累计已平仓的累计盈亏成功");
        return settlementVo;
    }
}
