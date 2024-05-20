package org.orient.otc.netty.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.dm.dto.CalendarStartEndDto;
import org.orient.otc.api.dm.dto.TradayAddDaysDto;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dto.*;
import org.orient.otc.api.enums.ReportTypeEnum;
import org.orient.otc.api.feign.PythonClient;
import org.orient.otc.api.quote.dto.risk.TradeRiskCacularResult;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.feign.VolatilityClient;
import org.orient.otc.api.quote.vo.VolatilityDataVO;
import org.orient.otc.api.quote.vo.VolatilityVO;
import org.orient.otc.api.vo.BucketedVegaVO;
import org.orient.otc.api.vo.PythonResult;
import org.orient.otc.api.vo.ScenarioQuoteVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.netty.dto.ScenarioQuoteDTO;
import org.orient.otc.netty.exception.BussinessException;
import org.orient.otc.netty.service.ScenarioService;
import org.orient.otc.netty.vo.UnderlyingVarietyVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author dzrh
 */
@Service
@Slf4j
public class ScenarioServiceImpl implements ScenarioService {

    @Resource
    private CalendarClient calendarClient;
    @Resource
    private PythonClient pythonClient;

    @Resource
    private VolatilityClient volatilityClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SystemConfigUtil systemConfigUtil;


    @Override
    public List<ScenarioQuoteVO> scenario(ScenarioQuoteDTO quoteDTO) {
        ScenarioDTO scenarioDTO = new ScenarioDTO();
        //设置通用模板配置
        scenarioDTO.setGeneral(getGeneral(quoteDTO));
        //交易数据
        List<Object> otcList = stringRedisTemplate.opsForHash().multiGet(RedisAdapter.TRADE_RISK_RESULT
                , Arrays.asList(quoteDTO.getTradeIdList().toArray()));

        List<TradeRiskCacularResult> tradeList = JSONObject.parseArray(otcList.toString(), TradeRiskCacularResult.class);
        tradeList.remove(null);
        BussinessException.E_400201.assertTrue(otcList.size() == tradeList.size());
        LocalDateTime localDateTime ;
        if (quoteDTO.getQuoteTime() != null) {
            localDateTime= systemConfigUtil.getTradeDay().atTime(quoteDTO.getQuoteTime());
        } else {
            localDateTime = systemConfigUtil.getTradeDay().atTime(14, 59, 0);
        }
        Set<String> underlyingCodeSet = tradeList.stream().map(TradeRiskCacularResult::getUnderlyingCode).collect(Collectors.toSet());
        Map<String, BigDecimal> priceMap = tradeList.stream().collect(
                Collectors.toMap(TradeRiskCacularResult::getUnderlyingCode, TradeRiskCacularResult::getLastPrice, (v1, v2) -> v2));
        List<VolatilityVO> volatilityVOList = volatilityClient.getVolListByCodeSet(underlyingCodeSet);
        Map<String, List<VolatilityDataVO>> volatilityVOMap = volatilityVOList.stream().filter(v -> v.getVolType() == VolTypeEnum.mid)
                .collect(Collectors.toMap(VolatilityVO::getUnderlyingCode, VolatilityVO::getData, (v1, v2) -> v2));
        scenarioDTO.setTradeData(CglibUtil.copyList(tradeList, TradeDataDTO::new, (s, t) -> {
            t.setTradingCode(s.getId());
            t.setCode(s.getUnderlyingCode());
            t.setTodayDate(localDateTime);
            t.setStartDate(s.getProductStartDate() != null ? s.getProductStartDate().atTime(15, 0, 0) : null);
            t.setEndDate(s.getMaturityDate() != null ? s.getMaturityDate().atTime(15, 0, 0) : null);
            t.setCurrentFut(s.getLastPrice());
            t.setStartFut(s.getEntryPrice());
            t.setR(s.getRiskFreeInterestRate());
            //分红率
            t.setQ(s.getDividendYield());
            t.setDividend(s.getDividendYield());
            t.setQuantity(s.getAvailableVolume());
            t.setCVS(CglibUtil.copyList(volatilityVOMap.getOrDefault(s.getUnderlyingCode(), new ArrayList<>()), VolatityDataDTO::new));
            t.setConstSgm(s.getRiskVol() != null ? BigDecimalUtil.percentageToBigDecimal(s.getRiskVol()) : BigDecimal.ZERO);
            //合约乘数
            t.setMultiplier(s.getMultiplier());
            //看涨看跌
            t.setFlag(s.getCallOrPut() != null ? s.getCallOrPut().name() : null);
            //观察日列表
            if (s.getObsDateList() != null && !s.getObsDateList().isEmpty()) {
                t.setObsDateList(CglibUtil.copyList(s.getObsDateList(), TradeObsDateDTO::new, (vo, dto) -> {
                    dto.setObsDate(vo.getObsDate().atTime(15, 0, 0));
                    if (vo.getPrice() == null) {
                        dto.setPrice(BigDecimal.ZERO);
                    }
                    //雪球期权的敲出价格
                    if (vo.getBarrierRelative() != null && vo.getBarrierRelative()) {
                        dto.setCallBarrier(BigDecimalUtil.percentageToBigDecimal(vo.getBarrier()).multiply(s.getEntryPrice()));
                        dto.setCallBarriershift(BigDecimalUtil.percentageToBigDecimal(vo.getBarrierShift()).multiply(s.getEntryPrice()));
                    } else {
                        dto.setCallBarrier(vo.getBarrier());
                        dto.setCallBarriershift(vo.getBarrierShift());
                    }
                    //敲出票息
                    if (vo.getRebateRate()!=null){
                        dto.setCoupon(BigDecimalUtil.percentageToBigDecimal(vo.getRebateRate()));
                    }
                    dto.setIsCouponannualized(vo.getRebateRateAnnulized());

                }));
            }
            //结算方式
            if (s.getSettleType() != null) {
                t.setIsCashsettle(s.getSettleType().getKey());
            }
            //敲出赔付
            if (s.getKnockoutRebate() != null) {
                t.setRebate(s.getKnockoutRebate());
            }else {
                t.setRebate(BigDecimal.ZERO);
            }
            //到期倍数
            if (s.getExpireMultiple()!=null){
                t.setExpiryLeverage(s.getExpireMultiple());
            }
            //是否为客户方向
            if (quoteDTO.getIsClient()) {
                if (s.getBuyOrSell() == BuyOrSellEnum.buy)
                    t.setSign(-1);
                else {
                    t.setSign(1);
                }
            } else {
                if (s.getBuyOrSell() == BuyOrSellEnum.buy)
                    t.setSign(1);
                else {
                    t.setSign(-1);
                }
            }
            if (s.getFixedPayment() == null) {
                t.setFixedPayment(BigDecimal.ZERO);
            }
            if (s.getStrikeRamp() == null) {
                t.setStrikeRamp(BigDecimal.ZERO);
            }
            //场内期权
            if (s.getTradeRiskCacularResultSourceType() == TradeRiskCacularResultSourceType.exchange) {
                if (s.getTradeRiskCacularResultType() == TradeRiskCacularResultType.european) {
                    t.setOptType("商品期货");
                } else {
                    t.setOptType("场内期权");
                }
            } else {
                switch (s.getOptionType()) {
                    case AIVanillaPricer:
                        t.setOptType("香草期权");
                        break;
                    case AIForwardPricer:
                        t.setOptType("远期");
                        break;
                    case AIPutAccPricer:
                        t.setFlag("accput");
                        t.setOptType("累计期权");
                        break;
                    case AICallAccPricer:
                        t.setFlag("acccall");
                        t.setOptType("累计期权");
                        break;
                    case AICallFixAccPricer:
                        t.setFlag("fpcall");
                        t.setOptType("累计期权");
                        break;
                    case AIPutFixAccPricer:
                        t.setFlag("fpput");
                        t.setOptType("累计期权");
                        break;
                    case AIEnAsianPricer:
                        t.setOptType("增强亚式");
                        break;
                    case AIAsianPricer:
                        t.setOptType("欧式亚式");
                        break;
                    case AIPutKOAccPricer:
                        t.setFlag("accput");
                        t.setDailyLeverage(s.getLeverage());
                        t.setOptType("熔断累计");
                        break;
                    case AICallKOAccPricer:
                        t.setFlag("acccall");
                        t.setDailyLeverage(s.getLeverage());
                        t.setOptType("熔断累计");
                        break;
                    case AICallFixKOAccPricer:
                        t.setFlag("fpcall");
                        t.setDailyLeverage(s.getLeverage());
                        t.setOptType("熔断累计");
                        break;
                    case AIPutFixKOAccPricer:
                        t.setFlag("fpput");
                        t.setDailyLeverage(s.getLeverage());
                        t.setOptType("熔断累计");
                        break;
                    case AIEnCallKOAccPricer:
                        t.setFlag("acccallplus");
                        t.setDailyLeverage(s.getLeverage());
                        t.setOptType("熔断累计");
                        break;
                    case AIEnPutKOAccPricer:
                        t.setFlag("accputplus");
                        t.setDailyLeverage(s.getLeverage());
                        t.setOptType("熔断累计");
                        break;
                    case AILimitLossesSnowBallCallPricer:
                    case AILimitLossesSnowBallPutPricer:
                    case AIBreakEvenSnowBallCallPricer:
                    case AIBreakEvenSnowBallPutPricer:
                    case AISnowBallCallPricer:
                    case AISnowBallPutPricer:
                        t.setNominal(s.getAvailableNotionalPrincipal());
                        //将价格转化为绝对价格
                        if (s.getKnockinBarrierRelative() != null && s.getKnockinBarrierRelative()) {
                            s.setKnockinBarrierValue(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getKnockinBarrierValue())));
                            if (s.getKnockinBarrierShift() != null) {
                                s.setKnockinBarrierShift(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getKnockinBarrierShift())));
                            }
                        }
                        if (s.getStrikeOnceKnockedinRelative() != null && s.getStrikeOnceKnockedinRelative()) {
                            s.setStrikeOnceKnockedinValue(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getStrikeOnceKnockedinValue())));
                            if (s.getStrikeOnceKnockedinShift() != null) {
                                s.setStrikeOnceKnockedinShift(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getStrikeOnceKnockedinShift())));
                            }
                        }
                        if (s.getStrike2OnceKnockedinRelative() != null && s.getStrike2OnceKnockedinRelative()) {
                            s.setStrike2OnceKnockedinValue(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getStrike2OnceKnockedinValue())));
                            if (s.getStrike2OnceKnockedinShift() != null) {
                                s.setStrike2OnceKnockedinShift(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getStrike2OnceKnockedinShift())));
                            }
                        }
                        //敲入价格
                        t.setBarrier(s.getKnockinBarrierValue()!=null?s.getKnockinBarrierValue():BigDecimal.ZERO);
                        t.setBarrierRamp(s.getKnockinBarrierShift()!=null?s.getKnockinBarrierShift():BigDecimal.ZERO);
                        //敲入行权价格
                        t.setKu(s.getStrikeOnceKnockedinValue()!=null?s.getStrikeOnceKnockedinValue():BigDecimal.ZERO);
                        t.setUstrikeRamp(s.getStrikeOnceKnockedinShift()!=null?s.getStrikeOnceKnockedinShift():BigDecimal.ZERO);

                        //红利票息
                        t.setBonus(BigDecimalUtil.percentageToBigDecimal(s.getBonusRateStructValue()));
                        t.setIsBonusAn(s.getBonusRateAnnulized());
                        //是否敲入
                        t.setIsKnockedin(s.getAlreadyKnockedIn()!=null?s.getAlreadyKnockedIn():Boolean.FALSE);
                        //返息率
                        t.setReturnRate(BigDecimalUtil.percentageToBigDecimal(s.getReturnRateStructValue()));
                        t.setIsReturnAn(s.getReturnRateAnnulized());
                        //看涨看跌
                        if (s.getOptionType()==OptionTypeEnum.AILimitLossesSnowBallCallPricer
                        ||s.getOptionType()==OptionTypeEnum.AIBreakEvenSnowBallCallPricer
                                ||s.getOptionType()==OptionTypeEnum.AISnowBallCallPricer){
                            t.setFlag("call");
                            //敲入行权价格2
                            t.setKd(s.getStrike2OnceKnockedinValue()!=null?s.getStrike2OnceKnockedinValue():BigDecimal.ZERO);
                            t.setDstrikeRamp(s.getStrike2OnceKnockedinShift()!=null?s.getStrike2OnceKnockedinShift():BigDecimal.ZERO);
                        }else {
                            t.setFlag("put");
                            //敲入行权价格2
                            t.setKd(s.getStrike2OnceKnockedinValue()!=null?s.getStrike2OnceKnockedinValue():s.getEntryPrice().pow(2));
                            t.setDstrikeRamp(s.getStrike2OnceKnockedinShift()!=null?s.getStrike2OnceKnockedinShift():BigDecimal.ZERO);
                        }
                        t.setOptType("雪球");
                        break;
                    default:
                        BussinessException.E_400101.assertTrue(false, s.getOptionType().getDesc());
                }
            }
        }));
        if (log.isDebugEnabled())
            log.debug("情景分析参数:{}", JSONObject.toJSONString(scenarioDTO));
        PythonResult<ScenarioQuoteVO> pythonResult = pythonClient.scenario(scenarioDTO);
        BussinessException.E_400101.assertTrue(pythonResult.getError_message().isEmpty(), JSONObject.toJSONString(pythonResult.getError_message()));
        for (ScenarioQuoteVO vo : pythonResult.getResults()) {
            vo.setSpotPriceShow(
                    priceMap.get(vo.getUnderlying())
                            .multiply(BigDecimal.ONE.add(vo.getSpotPrice()))
                            .setScale(2, RoundingMode.HALF_UP)
                            + "(" + vo.getSpotPrice().multiply(BigDecimal.valueOf(100)).setScale(2,RoundingMode.HALF_UP) + "%)");
        }

        return pythonResult.getResults();
    }

    @Override
    public List<BucketedVegaVO> bucketedVega(ScenarioQuoteDTO quoteDTO) {
        //交易数据
        List<Object> otcList = stringRedisTemplate.opsForHash().multiGet(RedisAdapter.TRADE_RISK_RESULT
                , Arrays.asList(quoteDTO.getTradeIdList().toArray()));
        LocalDateTime localDateTime ;
        if (quoteDTO.getQuoteTime() != null) {
            localDateTime= systemConfigUtil.getTradeDay().atTime(quoteDTO.getQuoteTime());
        } else {
            localDateTime = systemConfigUtil.getTradeDay().atTime(14, 59, 0);
        }
        List<TradeRiskCacularResult> tradeList = JSONObject.parseArray(otcList.toString(), TradeRiskCacularResult.class);
        tradeList.remove(null);
        BussinessException.E_400201.assertTrue(otcList.size() == tradeList.size());
        Set<String> underlyingCodeSet = tradeList.stream().map(TradeRiskCacularResult::getUnderlyingCode).collect(Collectors.toSet());
        Map<String, List<TradeRiskCacularResult>> underlyingCodeMap=  tradeList.stream().collect(Collectors.groupingBy(TradeRiskCacularResult::getUnderlyingCode));
        List<VolatilityVO> volatilityVOList = volatilityClient.getVolListByCodeSet(underlyingCodeSet);
        Map<String, List<VolatilityDataVO>> volatilityVOMap = volatilityVOList.stream().filter(v -> v.getVolType() == VolTypeEnum.mid)
                .collect(Collectors.toMap(VolatilityVO::getUnderlyingCode, VolatilityVO::getData, (v1, v2) -> v2));
        BucketedVegaDTO bucketedVegaDTO = new BucketedVegaDTO();
        List<BucketedVegaDTO.BucketedVega> bucketedVegaList = new ArrayList<>();
        for (Map.Entry<String, List<TradeRiskCacularResult>> entry : underlyingCodeMap.entrySet()){
            BucketedVegaDTO.BucketedVega bucketedVega = new BucketedVegaDTO.BucketedVega();
            BucketedVegaDTO.BucketedVega.BucketedVegaGeneral bucketedVegaGeneral= new BucketedVegaDTO.BucketedVega.BucketedVegaGeneral();
            bucketedVegaGeneral.setCode(entry.getKey());
            bucketedVegaGeneral.setVolList(CglibUtil.copyList(volatilityVOMap.getOrDefault(entry.getKey(), new ArrayList<>()), VolatityDataDTO::new));
            bucketedVega.setGeneral(bucketedVegaGeneral);
            bucketedVega.setTradeData(CglibUtil.copyList(entry.getValue(), TradeDataDTO::new, (s, t) -> {
                t.setTradingCode(s.getId());
                t.setCode(s.getUnderlyingCode());
                t.setTodayDate(localDateTime);
                t.setStartDate(s.getProductStartDate() != null ? s.getProductStartDate().atTime(15, 0, 0) : null);
                t.setEndDate(s.getMaturityDate() != null ? s.getMaturityDate().atTime(15, 0, 0) : null);
                t.setCurrentFut(s.getLastPrice());
                t.setStartFut(s.getEntryPrice());
                t.setR(s.getRiskFreeInterestRate());
                //分红率
                t.setQ(s.getDividendYield());
                t.setDividend(s.getDividendYield());
                t.setQuantity(s.getAvailableVolume());
                t.setConstSgm(s.getRiskVol() != null ? BigDecimalUtil.percentageToBigDecimal(s.getRiskVol()) : BigDecimal.ZERO);
                //合约乘数
                t.setMultiplier(s.getMultiplier());
                //看涨看跌
                t.setFlag(s.getCallOrPut() != null ? s.getCallOrPut().name() : null);
                //观察日列表
                if (s.getObsDateList() != null && !s.getObsDateList().isEmpty()) {
                    t.setObsDateList(CglibUtil.copyList(s.getObsDateList(), TradeObsDateDTO::new, (vo, dto) -> {
                        dto.setObsDate(vo.getObsDate().atTime(15, 0, 0));
                        if (vo.getPrice() == null) {
                            dto.setPrice(BigDecimal.ZERO);
                        }
                        //雪球期权的敲出价格
                        if (vo.getBarrierRelative() != null && vo.getBarrierRelative()) {
                            dto.setCallBarrier(BigDecimalUtil.percentageToBigDecimal(vo.getBarrier()).multiply(s.getEntryPrice()));
                            dto.setCallBarriershift(BigDecimalUtil.percentageToBigDecimal(vo.getBarrierShift()).multiply(s.getEntryPrice()));
                        } else {
                            dto.setCallBarrier(vo.getBarrier());
                            dto.setCallBarriershift(vo.getBarrierShift());
                        }
                        //敲出票息
                        if (vo.getRebateRate()!=null){
                            dto.setCoupon(BigDecimalUtil.percentageToBigDecimal(vo.getRebateRate()));
                        }
                        dto.setIsCouponannualized(vo.getRebateRateAnnulized());

                    }));
                }
                //是否为客户方向
                if (quoteDTO.getIsClient()) {
                    if (s.getBuyOrSell() == BuyOrSellEnum.buy)
                        t.setSign(-1);
                    else {
                        t.setSign(1);
                    }
                } else {
                    if (s.getBuyOrSell() == BuyOrSellEnum.buy)
                        t.setSign(1);
                    else {
                        t.setSign(-1);
                    }
                }
                //结算方式
                if (s.getSettleType() != null) {
                    t.setIsCashsettle(s.getSettleType().getKey());
                }
                //敲出赔付
                if (s.getKnockoutRebate() != null) {
                    t.setRebate(s.getKnockoutRebate());
                }else {
                    t.setRebate(BigDecimal.ZERO);
                }
                //到期倍数
                if (s.getExpireMultiple()!=null){
                    t.setExpiryLeverage(s.getExpireMultiple());
                }
                if (s.getFixedPayment() == null) {
                    t.setFixedPayment(BigDecimal.ZERO);
                }
                if (s.getStrikeRamp() == null) {
                    t.setStrikeRamp(BigDecimal.ZERO);
                }
                //场内期权
                if (s.getTradeRiskCacularResultSourceType() == TradeRiskCacularResultSourceType.exchange) {
                    if (s.getTradeRiskCacularResultType() == TradeRiskCacularResultType.european) {
                        t.setOptType("商品期货");
                    } else {
                        t.setOptType("场内期权");
                    }
                } else {
                    switch (s.getOptionType()) {
                        case AIVanillaPricer:
                            t.setOptType("香草期权");
                            break;
                        case AIForwardPricer:
                            t.setOptType("远期");
                            break;
                        case AIPutAccPricer:
                            t.setFlag("accput");
                            t.setOptType("累计期权");
                            break;
                        case AICallAccPricer:
                            t.setFlag("acccall");
                            t.setOptType("累计期权");
                            break;
                        case AICallFixAccPricer:
                            t.setFlag("fpcall");
                            t.setOptType("累计期权");
                            break;
                        case AIPutFixAccPricer:
                            t.setFlag("fpput");
                            t.setOptType("累计期权");
                            break;
                        case AIEnAsianPricer:
                            t.setOptType("增强亚式");
                            break;
                        case AIAsianPricer:
                            t.setOptType("欧式亚式");
                            break;
                        case AIPutKOAccPricer:
                            t.setFlag("accput");
                            t.setDailyLeverage(s.getLeverage());
                            t.setOptType("熔断累计");
                            break;
                        case AICallKOAccPricer:
                            t.setFlag("acccall");
                            t.setDailyLeverage(s.getLeverage());
                            t.setOptType("熔断累计");
                            break;
                        case AICallFixKOAccPricer:
                            t.setFlag("fpcall");
                            t.setDailyLeverage(s.getLeverage());
                            t.setOptType("熔断累计");
                            break;
                        case AIPutFixKOAccPricer:
                            t.setFlag("fpput");
                            t.setDailyLeverage(s.getLeverage());
                            t.setOptType("熔断累计");
                            break;
                        case AIEnCallKOAccPricer:
                            t.setFlag("acccallplus");
                            t.setDailyLeverage(s.getLeverage());
                            t.setOptType("熔断累计");
                            break;
                        case AIEnPutKOAccPricer:
                            t.setFlag("accputplus");
                            t.setDailyLeverage(s.getLeverage());
                            t.setOptType("熔断累计");
                            break;
                        case AILimitLossesSnowBallCallPricer:
                        case AILimitLossesSnowBallPutPricer:
                        case AIBreakEvenSnowBallCallPricer:
                        case AIBreakEvenSnowBallPutPricer:
                        case AISnowBallCallPricer:
                        case AISnowBallPutPricer:
                            t.setNominal(s.getAvailableNotionalPrincipal());
                            //将价格转化为绝对价格
                            if (s.getKnockinBarrierRelative() != null && s.getKnockinBarrierRelative()) {
                                s.setKnockinBarrierValue(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getKnockinBarrierValue())));
                                if (s.getKnockinBarrierShift() != null) {
                                    s.setKnockinBarrierShift(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getKnockinBarrierShift())));
                                }
                            }
                            if (s.getStrikeOnceKnockedinRelative() != null && s.getStrikeOnceKnockedinRelative()) {
                                s.setStrikeOnceKnockedinValue(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getStrikeOnceKnockedinValue())));
                                if (s.getStrikeOnceKnockedinShift() != null) {
                                    s.setStrikeOnceKnockedinShift(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getStrikeOnceKnockedinShift())));
                                }
                            }
                            if (s.getStrike2OnceKnockedinRelative() != null && s.getStrike2OnceKnockedinRelative()) {
                                s.setStrike2OnceKnockedinValue(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getStrike2OnceKnockedinValue())));
                                if (s.getStrike2OnceKnockedinShift() != null) {
                                    s.setStrike2OnceKnockedinShift(s.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(s.getStrike2OnceKnockedinShift())));
                                }
                            }
                            //敲入价格
                            t.setBarrier(s.getKnockinBarrierValue()!=null?s.getKnockinBarrierValue():BigDecimal.ZERO);
                            t.setBarrierRamp(s.getKnockinBarrierShift()!=null?s.getKnockinBarrierShift():BigDecimal.ZERO);
                            //敲入行权价格
                            t.setKu(s.getStrikeOnceKnockedinValue()!=null?s.getStrikeOnceKnockedinValue():BigDecimal.ZERO);
                            t.setUstrikeRamp(s.getStrikeOnceKnockedinShift()!=null?s.getStrikeOnceKnockedinShift():BigDecimal.ZERO);

                            //红利票息
                            t.setBonus(BigDecimalUtil.percentageToBigDecimal(s.getBonusRateStructValue()));
                            t.setIsBonusAn(s.getBonusRateAnnulized());
                            //是否敲入
                            t.setIsKnockedin(s.getAlreadyKnockedIn()!=null?s.getAlreadyKnockedIn():Boolean.FALSE);
                            //返息率
                            t.setReturnRate(BigDecimalUtil.percentageToBigDecimal(s.getReturnRateStructValue()));
                            t.setIsReturnAn(s.getReturnRateAnnulized());
                            //看涨看跌
                            if (s.getOptionType()==OptionTypeEnum.AILimitLossesSnowBallCallPricer
                                    ||s.getOptionType()==OptionTypeEnum.AIBreakEvenSnowBallCallPricer
                                    ||s.getOptionType()==OptionTypeEnum.AISnowBallCallPricer){
                                t.setFlag("call");
                                //敲入行权价格2
                                t.setKd(s.getStrike2OnceKnockedinValue()!=null?s.getStrike2OnceKnockedinValue():BigDecimal.ZERO);
                                t.setDstrikeRamp(s.getStrike2OnceKnockedinShift()!=null?s.getStrike2OnceKnockedinShift():BigDecimal.ZERO);
                            }else {
                                t.setFlag("put");
                                //敲入行权价格2
                                t.setKd(s.getStrike2OnceKnockedinValue()!=null?s.getStrike2OnceKnockedinValue():s.getEntryPrice().pow(2));
                                t.setDstrikeRamp(s.getStrike2OnceKnockedinShift()!=null?s.getStrike2OnceKnockedinShift():BigDecimal.ZERO);
                            }
                            t.setOptType("雪球");
                            break;
                        default:
                            BussinessException.E_400101.assertTrue(false, s.getOptionType().getDesc());
                    }
                }
            }));
            bucketedVegaList.add(bucketedVega);
        }
        bucketedVegaDTO.setData(bucketedVegaList);
        if (log.isDebugEnabled())
            log.debug("情景分析参数:{}", JSONObject.toJSONString(bucketedVegaDTO));
        PythonResult<BucketedVegaVO> pythonResult = pythonClient.bucketedVega(bucketedVegaDTO);
        BussinessException.E_400101.assertTrue(pythonResult.getError_message().isEmpty(), JSONObject.toJSONString(pythonResult.getError_message()));
       return pythonResult.getResults();
    }

    @Override
    public List<UnderlyingVarietyVO> getUnderlyingVarietyList() {
        //交易数据
        List<Object> otcList = stringRedisTemplate.opsForHash().values(RedisAdapter.TRADE_RISK_RESULT);
        List<TradeRiskCacularResult> tradeList = JSONObject.parseArray(otcList.toString(), TradeRiskCacularResult.class);
        List<UnderlyingVarietyVO> underlyingVarietyVOList = new ArrayList<>();
        tradeList=tradeList.stream().collect(
                collectingAndThen(
                        toCollection(()->new TreeSet<>(Comparator.comparing(TradeRiskCacularResult::getUnderlyingCode))),ArrayList::new)
                );
        tradeList.forEach(trade->{
            UnderlyingVarietyVO underlyingVarietyVO = new UnderlyingVarietyVO();
            underlyingVarietyVO.setUnderlyingCode(trade.getUnderlyingCode());
            underlyingVarietyVO.setVarietyCode(trade.getVarietyCode());
            underlyingVarietyVOList.add(underlyingVarietyVO);
        });
        return underlyingVarietyVOList;
    }

    private GeneralDTO getGeneral(ScenarioQuoteDTO quoteDTO) {
        GeneralDTO generalDTO = new GeneralDTO();
        generalDTO.setReportType(quoteDTO.getReportType());
        generalDTO.setIsFixedVol(quoteDTO.getIsFixedVol());
        //价格列表
        List<BigDecimal> priceList = new ArrayList<>();
        BigDecimal startPrice = BigDecimalUtil.percentageToBigDecimal(quoteDTO.getDownPrice());
        BigDecimal endPrice = BigDecimalUtil.percentageToBigDecimal(quoteDTO.getUpPrice());
        BigDecimal intervalPrice = BigDecimalUtil.percentageToBigDecimal(quoteDTO.getIntervalPrice());
        BussinessException.E_400203.assertTrue(startPrice.compareTo(endPrice) <= 0);
        for (; startPrice.compareTo(endPrice) <= 0; startPrice = startPrice.add(intervalPrice)) {
            priceList.add(startPrice);
        }
        generalDTO.setPriceList(priceList);
        if (quoteDTO.getReportType() == ReportTypeEnum.spotVol) {
            //波动率
            List<BigDecimal> volList = new ArrayList<>();
            BigDecimal startVol = BigDecimalUtil.percentageToBigDecimal(quoteDTO.getDownVol());
            BigDecimal endVol = BigDecimalUtil.percentageToBigDecimal(quoteDTO.getUpVol());
            BigDecimal intervalVol = BigDecimalUtil.percentageToBigDecimal(quoteDTO.getIntervalVol());
            BussinessException.E_400204.assertTrue(startVol.compareTo(endVol) <= 0);
            for (; startVol.compareTo(endVol) <= 0; startVol = startVol.add(intervalVol)) {
                volList.add(startVol);
            }
            generalDTO.setVolList(volList);
        } else {
            generalDTO.setVolList(new ArrayList<>());
        }
        if (quoteDTO.getReportType() == ReportTypeEnum.spotDate) {
            //日期
            List<String> dateTimeList = new ArrayList<>();
            LocalDate tradeDay = systemConfigUtil.getTradeDay();
            LocalDate endDate = calendarClient.tradeDayAddDays(TradayAddDaysDto.builder().date(tradeDay).days(quoteDTO.getDayCount()).build());
            CalendarStartEndDto calendarStartEndDto = new CalendarStartEndDto();
            calendarStartEndDto.setStartDate(tradeDay);
            calendarStartEndDto.setEndDate(endDate);
            List<LocalDate> dateList = calendarClient.getTradeDateList(calendarStartEndDto);
            LocalTime localTime;
            if (quoteDTO.getQuoteTime() != null) {
                localTime = quoteDTO.getQuoteTime();
            } else {
                localTime = LocalTime.of(15, 0, 0);
            }
            for (int i = 0; i < dateList.size(); i += quoteDTO.getIntervalDate()) {
                dateTimeList.add(LocalDateTimeUtil.format(dateList.get(i).atTime(localTime), DatePattern.NORM_DATETIME_FORMATTER));
            }
            generalDTO.setTestDate(dateTimeList);
        } else {
            generalDTO.setTestDate(new ArrayList<>());
        }
        return generalDTO;
    }
}
