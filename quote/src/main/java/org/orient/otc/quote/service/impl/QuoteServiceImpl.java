package org.orient.otc.quote.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.StopWatch;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.FileUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.vo.ClientLevelVo;
import org.orient.otc.api.dm.dto.TradayAddDaysDto;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.feign.UnderlyingQuoteClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.dm.vo.UnderlyingQuoteVO;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.quote.dto.VolatilityQueryDto;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.exception.BaseException;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.jni.dto.*;
import org.orient.otc.common.jni.util.JniUtil;
import org.orient.otc.common.jni.vo.*;
import org.orient.otc.quote.dto.quote.*;
import org.orient.otc.quote.dto.volatility.VolatityDataDto;
import org.orient.otc.quote.entity.TradeMng;
import org.orient.otc.quote.entity.Volatility;
import org.orient.otc.quote.enums.OpenOrCloseEnum;
import org.orient.otc.quote.enums.TradeTypeEnum;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.handler.ExcelFillCellMergeStrategy;
import org.orient.otc.quote.mapper.TradeMngMapper;
import org.orient.otc.quote.service.QuoteService;
import org.orient.otc.quote.service.VolatilityService;
import org.orient.otc.quote.util.VolatilityUtil;
import org.orient.otc.quote.vo.quote.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.orient.otc.quote.util.QuoteUtil.*;
import static org.orient.otc.quote.util.VolatilityUtil.getValatityDataByOffset;

/**
 * 计算服务实现
 */
@Service
@Slf4j
public class QuoteServiceImpl implements QuoteService {

    @Resource
    private JniUtil jniUtil;

    @Resource
    private VolatilityService volatilityService;
    @Resource
    private ClientClient clientClient;
    @Resource
    private UnderlyingManagerClient underlyingManagerClient;

    @Resource
    private UnderlyingQuoteClient underlyingQuoteClient;
    @Resource
    private MarketClient marketClient;
    @Resource
    private TradeMngMapper tradeMngMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SystemConfigUtil systemConfigUtil;

    @Resource
    private CalendarClient calendarClient;

    @Value("${template.quotation}")
    private String quotationTemplatePath;
//
//    @Resource
//    @Qualifier("asyncTaskExecutor")
//    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Override
    public List<QuoteStringResultVo> quote(QuoteCalculateDTO quoteCalculateDTO) {
        List<QuoteCalculateDetailDTO> quoteDtoList = quoteCalculateDTO.getQuoteList();
        //获取系统配置参数
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisAdapter.SYSTEM_CONFIG_INFO);
        Map<String, String> systemInfoMap = entries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
        //系统交易日
        LocalDate tradeDay = LocalDate.parse(systemInfoMap.get(SystemConfigEnum.tradeDay.name()));
        //股息率
        BigDecimal dividendYield = BigDecimalUtil.percentageToBigDecimal(new BigDecimal(systemInfoMap.get(SystemConfigEnum.dividendYield.name())));
        //无风险利率
        BigDecimal riskFreeInterestRate = BigDecimalUtil.percentageToBigDecimal(new BigDecimal(systemInfoMap.get(SystemConfigEnum.riskFreeInterestRate.name())));
        int mcNumberPaths = Integer.parseInt(systemInfoMap.get(SystemConfigEnum.mcNumberPaths.name()));
        int pdeTimeGrid = Integer.parseInt(systemInfoMap.get(SystemConfigEnum.pdeTimeGrid.name()));
        int pdeSpotGrid = Integer.parseInt(systemInfoMap.get(SystemConfigEnum.pdeSpotGrid.name()));
        List<QuoteResultVo> resultList = new ArrayList<>();
        for (QuoteCalculateDetailDTO quoteDto : quoteDtoList) {
            //获取计算时间
            quoteDto.setEvaluationTime(quoteDto.getTradeDate());
            long evaluationTime = getEvaluationTime(quoteDto, tradeDay);
            //校验计算参数是否符合规则
            checkQuoteInfo(quoteDto);
            UnderlyingManagerVO underlying = underlyingManagerClient.getUnderlyingByCode(quoteDto.getUnderlyingCode());
            BussinessException.E_300001.assertTrue(underlying.getUpDownLimit() != null);
            underlying.setUpDownLimit(BigDecimalUtil.percentageToBigDecimal(underlying.getUpDownLimit()));
            //查询合约股息率
            BigDecimal underlyingDividendYield = dividendYield;
            if (Objects.nonNull(underlying.getDividendYield())) {
                underlyingDividendYield = BigDecimalUtil.percentageToBigDecimal(underlying.getDividendYield());
            }
            underlying.setDividendYield(underlyingDividendYield);
            //获取保证金系数
            ClientLevelVo clientLevel = clientClient.getClientLevel(quoteDto.getClientId());
            BigDecimal marginRate = clientLevel.getMarginRate();
            //处理平仓
            TradeMng tradeMng = new TradeMng();
            if (Objects.nonNull(quoteCalculateDTO.getOpenOrClose()) && quoteCalculateDTO.getOpenOrClose() == OpenOrCloseEnum.close) {
                //计算平仓盈亏
                tradeMng = tradeMngMapper.selectOne(new LambdaQueryWrapper<TradeMng>().eq(TradeMng::getTradeCode, quoteDto.getTradeCode()
                ).eq(TradeMng::getIsDeleted, 0));
                BussinessException.E_300212.assertTrue(Objects.nonNull(tradeMng));
            }
            QuoteResultVo resultVo = new QuoteResultVo();
            resultVo.setSort(quoteDto.getSort());
            resultVo.setOptionType(quoteDto.getOptionType());
            resultList.add(resultVo);
            switch (quoteDto.getOptionType()) {
                case AIVanillaPricer:
                    quoteAIVanillaPricer(resultVo, quoteDto, riskFreeInterestRate, marginRate, evaluationTime, underlying);
                    break;
                case AIAsianPricer:
                    quoteAIAsianPricer(resultVo, quoteDto, riskFreeInterestRate, marginRate, evaluationTime, underlying);
                    break;
                case AIEnAsianPricer:
                    enAsianPricer(resultVo, quoteDto, riskFreeInterestRate, marginRate, evaluationTime, underlying);
                    break;
                case AICallAccPricer:
                case AIPutAccPricer:
                case AICallFixAccPricer:
                case AIPutFixAccPricer:
                    accumulatorPricer(resultVo, quoteDto, riskFreeInterestRate, marginRate, evaluationTime, underlying);
                    break;
                case AIEnCallKOAccPricer:
                case AIEnPutKOAccPricer:
                case AICallKOAccPricer:
                case AIPutKOAccPricer:
                case AICallFixKOAccPricer:
                case AIPutFixKOAccPricer:
                    koAccumulatorPricer(resultVo, quoteDto, riskFreeInterestRate, marginRate, evaluationTime, underlying,quoteCalculateDTO.getOpenOrClose(), tradeMng);
                    break;
                case AIForwardPricer:
                    forwardPricer(resultVo, quoteDto, underlying);
                    break;
                case AISnowBallCallPricer:
                case AISnowBallPutPricer:
                case AILimitLossesSnowBallCallPricer:
                case AILimitLossesSnowBallPutPricer:
                case AIBreakEvenSnowBallCallPricer:
                case AIBreakEvenSnowBallPutPricer:
                    snowBallPricer(resultVo, quoteDto, riskFreeInterestRate
                            , mcNumberPaths, pdeTimeGrid, pdeSpotGrid, evaluationTime, underlying
                            , quoteCalculateDTO.getOpenOrClose(), tradeMng);
                    break;
                case AIInsuranceAsianPricer:
                    insuranceAsianPricer(resultVo,quoteDto,riskFreeInterestRate,marginRate,evaluationTime,underlying);
                    break;
                default:
                    BussinessException.E_300102.doThrow("期权类型错误", quoteDto.getOptionType());
            }
            //处理平仓
            if (Objects.nonNull(quoteCalculateDTO.getOpenOrClose()) && quoteCalculateDTO.getOpenOrClose() == OpenOrCloseEnum.close) {
                //开仓的编号
                resultVo.setTradeCode(tradeMng.getTradeCode());
                if (tradeMng.getOptionType() == OptionTypeEnum.AICallAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AICallFixAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutFixAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AICallKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AICallFixKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutFixKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIEnCallKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIEnPutKOAccPricer) {
                    //累计期权特殊处理 平仓盈亏=开仓期权单价+平仓期权单价
                    resultVo.setProfitLoss(resultVo.getOptionPremium()
                            .add(tradeMng.getOptionPremium())
                            .negate().setScale(2, RoundingMode.HALF_UP));
                } else if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
                    //雪球期权特殊处理使用平仓波动率和平仓分红率传入so计算的pv*平仓名义本金
                    resultVo.setProfitLoss(resultVo.getOptionPremium());
                } else {
                    //其它期权类型 平仓盈亏=开仓期权单价+平仓期权单价
                    resultVo.setProfitLoss(resultVo.getOptionPremium().add(tradeMng.getOptionPremium())
                            .multiply(quoteDto.getTradeVolume()).negate().setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
        //处理组合类型特殊内容
        if (quoteCalculateDTO.getTradeType() == TradeTypeEnum.makeUp) {
            //校验组合的看涨看跌客户方向
            makeUpCheck(quoteCalculateDTO);
            //如果是组合的话需要求和一下
            QuoteResultVo resultVo = new QuoteResultVo();
            List<QuoteMakeUpTotalDTO> quoteMakeUpTotalDTOS = JSONArray.parseArray(JSONArray.toJSONString(resultList), QuoteMakeUpTotalDTO.class);
            QuoteMakeUpTotalVo makeUpTotal = getMakeUpTotal(quoteMakeUpTotalDTOS);
            BeanUtils.copyProperties(makeUpTotal, resultVo);
            //获取保证金系数
            ClientLevelVo clientLevel = clientClient.getClientLevel(quoteCalculateDTO.getQuoteList().get(0).getClientId());
            BigDecimal marginRate = clientLevel.getMarginRate();
            resultVo.setMargin(getMargin(quoteCalculateDTO, dividendYield, riskFreeInterestRate, marginRate));
            //sort为1表示汇总的数据，前端要求这样
            resultVo.setSort(1);
            resultList.add(0, resultVo);
        }

        //将BigDecimal转字符串(不足为补0),直接用bigDecimal，小数位不够不会补充0
        List<QuoteStringResultVo> quoteStringResultVos = new ArrayList<>();
        for (QuoteResultVo quoteResultVo : resultList) {
            QuoteStringResultVo quoteStringResultVo = new QuoteStringResultVo();
            quoteStringResultVo.setSort(quoteResultVo.getSort());
            quoteStringResultVo.setTradeCode(quoteResultVo.getTradeCode());
            quoteStringResultVo.setOptionType(quoteResultVo.getOptionType());
            //雪球的不用做处理直接返回
            if (OptionTypeEnum.getSnowBall().contains(quoteResultVo.getOptionType())) {
                quoteStringResultVo.setDay1PnL(quoteResultVo.getDay1PnL().toPlainString());
                quoteStringResultVo.setPv(quoteResultVo.getPv().toPlainString());
                quoteStringResultVo.setDelta(quoteResultVo.getDelta().toPlainString());
                quoteStringResultVo.setGamma(quoteResultVo.getGamma().toPlainString());
                quoteStringResultVo.setRho(quoteResultVo.getRho().toPlainString());
                quoteStringResultVo.setDividendRho(quoteResultVo.getDividendRho().toPlainString());
                quoteStringResultVo.setTheta(quoteResultVo.getTheta().toPlainString());
                quoteStringResultVo.setVega(quoteResultVo.getVega().toPlainString());
                quoteStringResultVo.setOptionPremium(quoteResultVo.getOptionPremium().toPlainString());
                quoteStringResultVo.setUseMargin(quoteResultVo.getUseMargin().toPlainString());
            } else {
                quoteStringResultVo.setDay1PnL(keepFourDecimalPlaces(quoteResultVo.getDay1PnL()));
                quoteStringResultVo.setMargin(keepTwoDecimalPlaces(quoteResultVo.getMargin()));
                quoteStringResultVo.setPv(keepTwoDecimalPlaces(quoteResultVo.getPv()));
                quoteStringResultVo.setDelta(keepFourDecimalPlaces(quoteResultVo.getDelta()));
                quoteStringResultVo.setGamma(keepFourDecimalPlaces(quoteResultVo.getGamma()));
                quoteStringResultVo.setRho(keepFourDecimalPlaces(quoteResultVo.getRho()));
                quoteStringResultVo.setTheta(keepFourDecimalPlaces(quoteResultVo.getTheta()));
                quoteStringResultVo.setVega(keepFourDecimalPlaces(quoteResultVo.getVega()));
                quoteStringResultVo.setOptionPremium(keepTwoDecimalPlaces(quoteResultVo.getOptionPremium()));
                quoteStringResultVo.setOptionPremiumPercent(keepThreeDecimalPlaces(quoteResultVo.getOptionPremiumPercent()));
                quoteStringResultVo.setTotalAmount(keepTwoDecimalPlaces(quoteResultVo.getTotalAmount()));
                quoteStringResultVo.setProfitLoss(keepTwoDecimalPlaces(quoteResultVo.getProfitLoss()));
            }
            quoteStringResultVos.add(quoteStringResultVo);
        }
        return quoteStringResultVos;
    }

    private void checkQuoteInfo(QuoteCalculateDetailDTO dto) {
        //除了远期都需要midVol
        if (dto.getOptionType() != OptionTypeEnum.AIForwardPricer) {
            BussinessException.E_300102.assertTrue(dto.getMidVol() != null, "midVol不能为空");
        }
        //校验混合方式结算的期权类型
        if (dto.getSettleType() == SettleTypeEnum.mix) {
            BussinessException.E_300102.assertTrue(OptionTypeEnum.checkHaveMix(dto.getOptionType()), "不支持混合方式结算");
        }
        //观察日历校验
        if (dto.getTradeObsDateList() != null && !dto.getTradeObsDateList().isEmpty()) {
//            BussinessException.E_300102.assertTrue(!dto.getTradeDate()
//                            .isAfter(dto.getTradeObsDateList()
//                                    .get(0).getObsDate())
//                    , "第一个观察日必须大于或等于交易日");
            BussinessException.E_300102.assertTrue(!dto.getMaturityDate()
                            .isBefore(dto.getTradeObsDateList()
                                    .get(dto.getTradeObsDateList().size() - 1).getObsDate())
                    , "最后一个观察日必须小于或等于到期日");
        }
        //熔断累计期权校验
        if (OptionTypeEnum.getOrdinaryKOOptionType().contains(dto.getOptionType())) {
            BussinessException.E_300102.assertTrue(dto.getKnockoutRebate() != null, "熔断累计期权敲出赔付不能为空");
            BussinessException.E_300102.assertTrue(dto.getExpireMultiple() != null, "熔断累计期权到期倍数不能为空");
        }
        switch (dto.getOptionType()) {
            case AICallAccPricer:
            case AICallFixAccPricer:
            case AICallKOAccPricer:
            case AICallFixKOAccPricer:
            case AIEnCallKOAccPricer:
                if(dto.getBarrier()==null){
                    dto.setBarrier(BigDecimal.ZERO);
                }
                BussinessException.E_300102.assertTrue( dto.getBarrier().equals(BigDecimal.ZERO)
                                || dto.getBarrier().compareTo(dto.getStrike()) > 0,
                        "敲出价格必须为空或者大于行权价格");
                break;
            case AIPutAccPricer:
            case AIPutKOAccPricer:
            case AIPutFixKOAccPricer:
            case AIPutFixAccPricer:
            case AIEnPutKOAccPricer:
                if(dto.getBarrier()==null){
                    dto.setBarrier(BigDecimal.ZERO);
                }
                BussinessException.E_300102.assertTrue(dto.getBarrier().equals(BigDecimal.ZERO)
                                || dto.getBarrier().compareTo(dto.getStrike()) < 0,
                        "敲出价格必须为空或者小于行权价格");
                break;
            case AILimitLossesSnowBallCallPricer:
                BussinessException.E_300102.assertNotNull(dto.getStrike2OnceKnockedinValue(),"限亏雪球敲入行权价格2不能为空");
                BigDecimal callStrike2 = dto.getStrike2OnceKnockedinValue();
                if (dto.getStrike2OnceKnockedinRelative()) {
                    callStrike2 = BigDecimalUtil.percentageToBigDecimal(dto.getStrike2OnceKnockedinValue()).multiply(dto.getEntryPrice());
                }
                BigDecimal callStrike = dto.getStrikeOnceKnockedinValue();
                if (dto.getStrikeOnceKnockedinRelative()) {
                    callStrike = BigDecimalUtil.percentageToBigDecimal(dto.getStrikeOnceKnockedinValue()).multiply(dto.getEntryPrice());
                }
                BussinessException.E_300102.assertTrue(
                        callStrike2.compareTo(callStrike) <= 0
                        , "看涨雪球行权价格2必须小于或等于行权价格1"
                );
                break;
            case AILimitLossesSnowBallPutPricer:
                BussinessException.E_300102.assertNotNull(dto.getStrike2OnceKnockedinValue(),"限亏雪球敲入行权价格2不能为空");
                BigDecimal putStrike2 = dto.getStrike2OnceKnockedinValue();
                if (dto.getStrike2OnceKnockedinRelative()) {
                    putStrike2 = BigDecimalUtil.percentageToBigDecimal(dto.getStrike2OnceKnockedinValue()).multiply(dto.getEntryPrice());
                }
                BigDecimal putStrike = dto.getStrikeOnceKnockedinValue();
                if (dto.getStrikeOnceKnockedinRelative()) {
                    putStrike = BigDecimalUtil.percentageToBigDecimal(dto.getStrikeOnceKnockedinValue()).multiply(dto.getEntryPrice());
                }
                BussinessException.E_300102.assertTrue(
                        putStrike2.compareTo(putStrike) >= 0
                        , "看跌雪球行权价格2必须大于或等于行权价格1"
                );
                break;
        }
    }

    /**
     * 校验是否都为相对或者绝对
     * @param quoteDto 计算参数
     * @return true 校验通过 false校验失败
     */
    private Boolean checkRelative(QuoteCalculateDetailDTO quoteDto) {
        List<Boolean> relativeList = quoteDto.getTradeObsDateList().stream().map(TradeObsDateDto::getBarrierRelative).collect(Collectors.toList());
        relativeList.add(quoteDto.getKnockinBarrierRelative());
        relativeList.add(quoteDto.getStrikeOnceKnockedinRelative());
        relativeList.add(quoteDto.getStrike2OnceKnockedinRelative());
        relativeList = relativeList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        long count = relativeList.stream().distinct().count();
        return count == 1;
    }

    /**
     * 获取定价计算时间戳
     * @param quoteDto 定价参数
     * @param tradeDay 系统交易日
     * @return 定价时间戳
     */
    private long getEvaluationTime(QuoteCalculateDetailDTO quoteDto, LocalDate tradeDay) {
        if (Objects.nonNull(quoteDto.getTradeTime())) {
            //传交易时间用于测试用例
            return LocalDateTime.of(quoteDto.getEvaluationTime(), quoteDto
                    .getTradeTime()).toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
        } else {
            //系统交易日
            if (tradeDay.isEqual(quoteDto.getEvaluationTime())) {
                return LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
            } else {
                //当定价日期非系统交易日时,定价时间取前一个交易日的15:00:00
                LocalDate evaluationDate = calendarClient.tradeDayAddDays(TradayAddDaysDto.builder().date(quoteDto.getEvaluationTime()).days(-1).build());
                return LocalDateTime.of(evaluationDate, LocalTime.of(15, 0, 0)).toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
//                //当前时间在12点到20点之间，evaluationTime = tradeDate+14：00，否则evaluationTime = tradeDate + 10：00
//                if (LocalTime.now().isAfter(LocalTime.of(12, 0)) && LocalTime.now().isBefore(LocalTime.of(20, 0))) {
//                    return LocalDateTime.of(quoteDto.getEvaluationTime(), LocalTime.of(14, 0)).toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
//                } else {
//                    return LocalDateTime.of(quoteDto.getEvaluationTime(), LocalTime.of(10, 0)).toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
//                }

            }
        }
    }

    /**
     * 香草计算结果
     * @param resultVo             计算结果
     * @param quoteDto             定价计算明细
     * @param riskFreeInterestRate 无风险利率
     * @param marginRate           保证金系数
     * @param evaluationTime       计算时间
     * @param underlying           合约详情
     */
    private void quoteAIVanillaPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto,
                                      BigDecimal riskFreeInterestRate, BigDecimal marginRate, long evaluationTime, UnderlyingManagerVO underlying) {
        AIVanillaPricerRequest aiVanillaPricerRequestDto = new AIVanillaPricerRequest();
        aiVanillaPricerRequestDto.setOptionType(quoteDto.getCallOrPut().name());
        aiVanillaPricerRequestDto.setStrike(quoteDto.getStrike().doubleValue());
        aiVanillaPricerRequestDto.setVolatility(BigDecimalUtil.percentageToBigDecimal(quoteDto.getMidVol()).doubleValue());
        aiVanillaPricerRequestDto.setRiskFreeInterestRate(riskFreeInterestRate.doubleValue());
        aiVanillaPricerRequestDto.setDividendYield(underlying.getDividendYield().doubleValue());
        aiVanillaPricerRequestDto.setUnderlyingPrice(quoteDto.getEntryPrice().doubleValue());
        aiVanillaPricerRequestDto.setEvaluationTime(evaluationTime);
        aiVanillaPricerRequestDto.setExpiryTime(quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());

        AIVanillaPricerResult aiVanillaPricerResult = jniUtil.AIVanillaPricer(aiVanillaPricerRequestDto);

        copySoResult(resultVo, quoteDto, underlying, aiVanillaPricerResult.getPv(), aiVanillaPricerResult.getGamma()
                , aiVanillaPricerResult.getDelta(), aiVanillaPricerResult.getRhoPercentage()
                , aiVanillaPricerResult.getDividendRhoPercentage()
                , aiVanillaPricerResult.getVegaPercentage()
                , aiVanillaPricerResult.getThetaPerDay());

        //计算期权单价
        AIVanillaPricerRequest newAiVanillaPricerRequestDto1 = new AIVanillaPricerRequest();
        BeanUtils.copyProperties(aiVanillaPricerRequestDto, newAiVanillaPricerRequestDto1);
        BussinessException.E_300101.assertTrue(Objects.nonNull(quoteDto.getTradeVol()), "波动率没传");
        newAiVanillaPricerRequestDto1.setVolatility(BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue());
        AIVanillaPricerResult tradeAiVanillaPricerResult = jniUtil.AIVanillaPricer(newAiVanillaPricerRequestDto1);
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            resultVo.setOptionPremium(BigDecimal.valueOf(tradeAiVanillaPricerResult.getPv()).setScale(2, RoundingMode.HALF_UP));
        } else {
            resultVo.setOptionPremium(BigDecimal.valueOf(tradeAiVanillaPricerResult.getPv()).setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(-1)));
        }
        resultVo.setOptionPremiumPercent((resultVo.getOptionPremium().divide(quoteDto.getEntryPrice(), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));
        //计算成交金额
        resultVo.setTotalAmount(resultVo.getOptionPremium().multiply(quoteDto.getTradeVolume()));

        resultVo.setDay1PnL(resultVo.getTotalAmount().add(resultVo.getPv()).setScale(4, RoundingMode.HALF_UP));
        //求保证金
        /*
         * max{(γmax{-PV(F_0 )+PV(F̅ ),0},γmax{-PV(F_0 )+PV(F_),0} )}
         * F̅=(1+α)F_0
         * F_=(1-α)F_0
         * α为涨跌停比例
         * F_0为期初价格
         * γ为保证金系数
         */
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.sell) {
            AIVanillaPricerRequest newAiVanillaPricerRequestDto = new AIVanillaPricerRequest();
            BeanUtils.copyProperties(aiVanillaPricerRequestDto, newAiVanillaPricerRequestDto);

            BigDecimal upPrice = underlying.getUpDownLimit().add(BigDecimal.valueOf(1)).multiply(quoteDto.getEntryPrice());
            BigDecimal downPrice = BigDecimal.valueOf(1).subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
            newAiVanillaPricerRequestDto.setUnderlyingPrice(upPrice.doubleValue());
            double upPv = jniUtil.AIVanillaPricer(newAiVanillaPricerRequestDto).getPv();
            newAiVanillaPricerRequestDto.setUnderlyingPrice(downPrice.doubleValue());
            double downPv = jniUtil.AIVanillaPricer(newAiVanillaPricerRequestDto).getPv();
            BigDecimal margin = marginRate.multiply(BigDecimal.valueOf(upPv)
                            .subtract(BigDecimal.valueOf(aiVanillaPricerResult.getPv())).max(BigDecimal.valueOf(0)))
                    .max(marginRate.multiply(BigDecimal.valueOf(downPv)
                            .subtract(BigDecimal.valueOf(aiVanillaPricerResult.getPv())).max(BigDecimal.valueOf(0))))
                    .setScale(2, RoundingMode.HALF_UP);
            resultVo.setMargin(margin);
        } else {
            resultVo.setMargin(BigDecimal.ZERO);
        }
    }

    /**
     * 亚式期权计算
     * @param resultVo             计算结果
     * @param quoteDto             定价计算明细
     * @param riskFreeInterestRate 无风险利率
     * @param marginRate           保证金系数
     * @param evaluationTime       计算时间
     * @param underlying           合约详情
     */
    private void quoteAIAsianPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto,
                                    BigDecimal riskFreeInterestRate, BigDecimal marginRate, long evaluationTime, UnderlyingManagerVO underlying) {

        ObserveSchedule[] observeScheduleArray = getObserveScheduleArray(quoteDto.getTradeObsDateList());

        AIAsianPricerRequest aiAsianPricerRequest = new AIAsianPricerRequest(quoteDto.getCallOrPut().name(),
                quoteDto.getEntryPrice().doubleValue(),
                evaluationTime,
                quoteDto.getStrike().doubleValue(),
                quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond(),
                riskFreeInterestRate.doubleValue(),
                BigDecimalUtil.percentageToBigDecimal(quoteDto.getMidVol()).doubleValue(),
                observeScheduleArray.length);
        AIAsianPricerResult aiAsianPricerResult = jniUtil.AIAsianPricer(aiAsianPricerRequest, observeScheduleArray);


        copySoResult(resultVo, quoteDto, underlying, aiAsianPricerResult.getPv(), aiAsianPricerResult.getGamma()
                , aiAsianPricerResult.getDelta(), aiAsianPricerResult.getRhoPercentage()
                , 0.0
                , aiAsianPricerResult.getVegaPercentage(), aiAsianPricerResult.getThetaPerDay());

        //计算期权单价
        AIAsianPricerRequest newAiAsianPricerRequest1 = new AIAsianPricerRequest();
        BeanUtils.copyProperties(aiAsianPricerRequest, newAiAsianPricerRequest1);
        BussinessException.E_300101.assertTrue(Objects.nonNull(quoteDto.getTradeVol()), "波动率没传");
        newAiAsianPricerRequest1.setVolatility(BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue());
        AIAsianPricerResult tradeAiAsianPricerResult = jniUtil.AIAsianPricer(newAiAsianPricerRequest1, observeScheduleArray);
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            resultVo.setOptionPremium(BigDecimal.valueOf(tradeAiAsianPricerResult.getPv()).setScale(2, RoundingMode.HALF_UP));
        } else {
            resultVo.setOptionPremium(BigDecimal.valueOf(tradeAiAsianPricerResult.getPv()).setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(-1)));
        }
        resultVo.setOptionPremiumPercent((resultVo.getOptionPremium().divide(quoteDto.getEntryPrice(), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));
        //计算成交金额
        resultVo.setTotalAmount(resultVo.getOptionPremium().multiply(quoteDto.getTradeVolume()));
        resultVo.setDay1PnL(resultVo.getTotalAmount().add(resultVo.getPv()).setScale(4, RoundingMode.HALF_UP));

        //求保证金
        /*
         * max{(γmax{-PV(F_0 )+PV(F̅ ),0},γmax{-PV(F_0 )+PV(F_),0} )}
         * F̅=(1+α)F_0
         * F_=(1-α)F_0
         * α为涨跌停比例
         * F_0为期初价格
         * γ为保证金系数
         */
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.sell) {
            AIAsianPricerRequest newAiAsianPricerRequest = new AIAsianPricerRequest();
            BeanUtils.copyProperties(aiAsianPricerRequest, newAiAsianPricerRequest);
            BigDecimal upPrice = underlying.getUpDownLimit().add(BigDecimal.ONE).multiply(quoteDto.getEntryPrice());
            BigDecimal downPrice = BigDecimal.ONE.subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
            newAiAsianPricerRequest.setUnderlyingPrice(upPrice.doubleValue());
            double upPricePv = jniUtil.AIAsianPricer(newAiAsianPricerRequest, observeScheduleArray).getPv();
            newAiAsianPricerRequest.setUnderlyingPrice(downPrice.doubleValue());
            double downPricePv = jniUtil.AIAsianPricer(newAiAsianPricerRequest, observeScheduleArray).getPv();


            BigDecimal margin = marginRate.multiply(BigDecimal.valueOf(upPricePv).subtract(BigDecimal.valueOf(aiAsianPricerResult.getPv())).max(BigDecimal.valueOf(0)))
                    .max(marginRate.multiply(BigDecimal.valueOf(downPricePv).subtract(BigDecimal.valueOf(aiAsianPricerResult.getPv())).max(BigDecimal.valueOf(0))))
                    .setScale(2, RoundingMode.HALF_UP);

            resultVo.setMargin(margin);
        } else {
            resultVo.setMargin(BigDecimal.ZERO);
        }
    }

    /**
     * 增强亚式期权计算
     * @param resultVo             计算结果
     * @param quoteDto             定价计算明细
     * @param riskFreeInterestRate 无风险利率
     * @param marginRate           保证金系数
     * @param evaluationTime       计算时间
     * @param underlying           合约详情
     */
    private void enAsianPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto,
                               BigDecimal riskFreeInterestRate, BigDecimal marginRate, long evaluationTime, UnderlyingManagerVO underlying) {
        ObserveSchedule[] observeScheduleArray = getObserveScheduleArray(quoteDto.getTradeObsDateList());

        AIEnhancedAsianPricerRequest request = new AIEnhancedAsianPricerRequest();
        request.setConstantVol(0);
        request.setExpiryTime(quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
        request.setStrike(quoteDto.getStrike().doubleValue());
        request.setEvaluationTime(evaluationTime);
        request.setOptionType(quoteDto.getCallOrPut().name());
        request.setUnderlyingPrice(quoteDto.getEntryPrice().doubleValue());
        request.setRiskFreeInterestRate(riskFreeInterestRate.doubleValue());
        request.setScenarioPrice(0);
        request.setValueType("a");
        request.setTotalObservations(observeScheduleArray.length);
        request.setIsCashSettled(quoteDto.getSettleType().getKey());
        request.setEnhancedStrike(quoteDto.getEnhancedStrike().doubleValue());
        VolatilityQueryDto volatilityQueryDto = new VolatilityQueryDto();
        volatilityQueryDto.setQuotationDate(quoteDto.getTradeDate());
        volatilityQueryDto.setUnderlyingCode(quoteDto.getUnderlyingCode());
        List<Volatility> volatilityList = volatilityService.getVolatility(volatilityQueryDto);
        Volatility midVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.mid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "mid没配置"));
        List<VolatityDataDto> volatityDataList;
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.sell) {
            Volatility bidVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.bid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "bid没配置"));
            volatityDataList = getValatityDataByOffset(midVolatility.getData(), bidVolatility.getData());
        } else {
            Volatility askVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.ask).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "ask没配置"));
            volatityDataList = getValatityDataByOffset(midVolatility.getData(), askVolatility.getData());
        }

        VolSurface midVolSurface = VolatilityUtil.getVolSurface(midVolatility.getData());

        AIEnhancedAsianPricerResult aiEnhancedAsianPricerResult = jniUtil.AIEnhancedAsianPricer(request, observeScheduleArray, midVolSurface);

        copySoResult(resultVo, quoteDto, underlying, aiEnhancedAsianPricerResult.getPv()
                , aiEnhancedAsianPricerResult.getGamma(), aiEnhancedAsianPricerResult.getDelta()
                , aiEnhancedAsianPricerResult.getRhoPercentage(), aiEnhancedAsianPricerResult.getDividendRhoPercentage()
                , aiEnhancedAsianPricerResult.getVegaPercentage(), aiEnhancedAsianPricerResult.getThetaPerDay());

        //计算期权单价
        AIEnhancedAsianPricerRequest newAIEnhancedAsianPricerRequest1 = new AIEnhancedAsianPricerRequest();
        BeanUtils.copyProperties(request, newAIEnhancedAsianPricerRequest1);
        newAIEnhancedAsianPricerRequest1.setConstantVol(Objects.nonNull(quoteDto.getTradeVol()) ? BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue() : 0);
        AIEnhancedAsianPricerResult tradeAiEnhancedAsianPricerResult1 = jniUtil.AIEnhancedAsianPricer(newAIEnhancedAsianPricerRequest1, observeScheduleArray, VolatilityUtil.getVolSurface(volatityDataList));
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            resultVo.setOptionPremium(BigDecimal.valueOf(tradeAiEnhancedAsianPricerResult1.getPv()).setScale(2, RoundingMode.HALF_UP));
        } else {
            resultVo.setOptionPremium(BigDecimal.valueOf(tradeAiEnhancedAsianPricerResult1.getPv()).setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(-1)));
        }
        resultVo.setOptionPremiumPercent((resultVo.getOptionPremium().divide(quoteDto.getEntryPrice(), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));
        //计算成交金额
        resultVo.setTotalAmount(resultVo.getOptionPremium().multiply(quoteDto.getTradeVolume()));
        resultVo.setDay1PnL(resultVo.getTotalAmount().add(resultVo.getPv()).setScale(4, RoundingMode.HALF_UP));

        //求保证金
        /*
         * max{(γmax{-PV(F_0 )+PV(F̅ ),0},γmax{-PV(F_0 )+PV(F_),0} )}
         * F̅=(1+α)F_0
         * F_=(1-α)F_0
         * α为涨跌停比例
         * F_0为期初价格
         * γ为保证金系数
         */
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.sell) {
            AIEnhancedAsianPricerRequest newAIEnhancedAsianPricerRequest = new AIEnhancedAsianPricerRequest();
            BeanUtils.copyProperties(request, newAIEnhancedAsianPricerRequest);

            BigDecimal upPrice = underlying.getUpDownLimit().add(BigDecimal.valueOf(1)).multiply(quoteDto.getEntryPrice());
            BigDecimal downPrice = BigDecimal.valueOf(1).subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
            newAIEnhancedAsianPricerRequest.setUnderlyingPrice(upPrice.doubleValue());
            double upPricePv = jniUtil.AIEnhancedAsianPricer(newAIEnhancedAsianPricerRequest, observeScheduleArray, midVolSurface).getPv();
            newAIEnhancedAsianPricerRequest.setUnderlyingPrice(downPrice.doubleValue());
            double downPricePv = jniUtil.AIEnhancedAsianPricer(newAIEnhancedAsianPricerRequest, observeScheduleArray, midVolSurface).getPv();


            BigDecimal margin = marginRate.multiply(BigDecimal.valueOf(upPricePv).subtract(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getPv())).max(BigDecimal.valueOf(0)))
                    .max(marginRate.multiply(BigDecimal.valueOf(downPricePv).subtract(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getPv()))).max(BigDecimal.valueOf(0)))
                    .setScale(2, RoundingMode.HALF_UP);

            resultVo.setMargin(margin);
        } else {
            resultVo.setMargin(BigDecimal.ZERO);
        }
    }

    /**
     * 累计期权计算
     * @param resultVo             计算结果
     * @param quoteDto             定价计算明细
     * @param riskFreeInterestRate 无风险利率
     * @param marginRate           保证金系数
     * @param evaluationTime       计算时间
     * @param underlying           合约详情
     */
    private void accumulatorPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto, BigDecimal riskFreeInterestRate
            , BigDecimal marginRate, long evaluationTime, UnderlyingManagerVO underlying) {
        ObserveSchedule[] observeScheduleArray = getObserveScheduleArray(quoteDto.getTradeObsDateList());

        String accumulatorType = getAccumulatorType(quoteDto.getOptionType());

        BussinessException.E_300102.assertNotNull(quoteDto.getBasicQuantity(), "每日价格不能为空");
        BussinessException.E_300102.assertNotNull(quoteDto.getSettleType(), "结算方式不能为空");
        AIAccumulatorPricerRequest aiAccumulatorPricerRequest = new AIAccumulatorPricerRequest(
                accumulatorType,
                "a",
                quoteDto.getBuyOrSell() == BuyOrSellEnum.buy ? -1 : 1,
                quoteDto.getBasicQuantity().abs().doubleValue(),
                quoteDto.getEntryPrice().doubleValue(),
                quoteDto.getStrike().doubleValue(),
                evaluationTime,
                quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond(),
                0,
                quoteDto.getSettleType().getKey(),
                riskFreeInterestRate.doubleValue(),
                quoteDto.getLeverage().doubleValue(),
                quoteDto.getFixedPayment() == null ? 0.0 : quoteDto.getFixedPayment().doubleValue(),
                quoteDto.getBarrier().doubleValue(),
                Objects.nonNull(quoteDto.getStrikeRamp()) ? quoteDto.getStrikeRamp().doubleValue() : 0,
                quoteDto.getBarrierRamp().doubleValue(),
                observeScheduleArray.length,
                0);
        VolatilityQueryDto volatilityQueryDto = new VolatilityQueryDto();
        volatilityQueryDto.setQuotationDate(quoteDto.getTradeDate());
        volatilityQueryDto.setUnderlyingCode(quoteDto.getUnderlyingCode());
        List<Volatility> volatilityList = volatilityService.getVolatility(volatilityQueryDto);
        Volatility midVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.mid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "mid没配置"));
        List<VolatityDataDto> volatityDataList;
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            Volatility bidVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.bid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "bid没配置"));
            volatityDataList = getValatityDataByOffset(midVolatility.getData(), bidVolatility.getData());
        } else {
            Volatility askVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.ask).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "ask没配置"));
            volatityDataList = getValatityDataByOffset(midVolatility.getData(), askVolatility.getData());
        }

        VolSurface midVolSurface = VolatilityUtil.getVolSurface(midVolatility.getData());

        AIAccumulatorPricerResult aiAccumulatorPricerResult = jniUtil.AIAccumulatorPricer(aiAccumulatorPricerRequest, observeScheduleArray, midVolSurface);
        //计算保证金
        AIAccumulatorPricerResult pricerResult;
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            //客户买入累计累沽期权
            //（包括固定赔付累沽和熔断累沽、熔断固定赔付累沽）->标的涨停
            if (quoteDto.getOptionType() == OptionTypeEnum.AIPutAccPricer
                    || quoteDto.getOptionType() == OptionTypeEnum.AIPutFixAccPricer) {
                AIAccumulatorPricerRequest upPricePVRequest = CglibUtil.copy(aiAccumulatorPricerRequest, AIAccumulatorPricerRequest.class);
                BigDecimal upPrice = underlying.getUpDownLimit().add(BigDecimal.ONE).multiply(quoteDto.getEntryPrice());
                upPricePVRequest.setUnderlyingPrice(upPrice.doubleValue());
                pricerResult = jniUtil.AIAccumulatorPricer(upPricePVRequest, observeScheduleArray, midVolSurface);
            } else {
                AIAccumulatorPricerRequest downPricePVRequest = CglibUtil.copy(aiAccumulatorPricerRequest, AIAccumulatorPricerRequest.class);
                BigDecimal downPrice = BigDecimal.valueOf(1).subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
                downPricePVRequest.setUnderlyingPrice(downPrice.doubleValue());
                pricerResult = jniUtil.AIAccumulatorPricer(downPricePVRequest, observeScheduleArray, midVolSurface);
            }
        } else {
            if (quoteDto.getOptionType() == OptionTypeEnum.AICallAccPricer
                    || quoteDto.getOptionType() == OptionTypeEnum.AICallFixAccPricer) {
                AIAccumulatorPricerRequest upPricePVRequest = CglibUtil.copy(aiAccumulatorPricerRequest, AIAccumulatorPricerRequest.class);
                BigDecimal upPrice = underlying.getUpDownLimit().add(BigDecimal.ONE).multiply(quoteDto.getEntryPrice());
                upPricePVRequest.setUnderlyingPrice(upPrice.doubleValue());
                pricerResult = jniUtil.AIAccumulatorPricer(upPricePVRequest, observeScheduleArray, midVolSurface);
            } else {
                AIAccumulatorPricerRequest downPricePVRequest = CglibUtil.copy(aiAccumulatorPricerRequest, AIAccumulatorPricerRequest.class);
                BigDecimal downPrice = BigDecimal.valueOf(1).subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
                downPricePVRequest.setUnderlyingPrice(downPrice.doubleValue());
                pricerResult = jniUtil.AIAccumulatorPricer(downPricePVRequest, observeScheduleArray, midVolSurface);
            }
        }
        //涨跌停的PV
        double pricePv = pricerResult.getPv();
        BigDecimal max = BigDecimal.valueOf(pricePv).max(BigDecimal.ZERO).multiply(marginRate);
        BigDecimal margin = max.setScale(2, RoundingMode.HALF_UP);
        resultVo.setMargin(margin);
        copySoResult(resultVo, quoteDto, underlying, aiAccumulatorPricerResult.getPv()
                , aiAccumulatorPricerResult.getGamma(), aiAccumulatorPricerResult.getDelta()
                , aiAccumulatorPricerResult.getRhoPercentage(), aiAccumulatorPricerResult.getDividendRhoPercentage()
                , aiAccumulatorPricerResult.getVegaPercentage(), aiAccumulatorPricerResult.getThetaPerDay());

        //计算期权单价
        AIAccumulatorPricerRequest optionPremiumPVRequest = CglibUtil.copy(aiAccumulatorPricerRequest, AIAccumulatorPricerRequest.class);
        optionPremiumPVRequest.setConstantVol(Objects.nonNull(quoteDto.getTradeVol()) ? BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue() : 0);
        VolSurface tradeVolSurface = VolatilityUtil.getVolSurface(volatityDataList);
        AIAccumulatorPricerResult tradeAiAccumulatorPricerResult = jniUtil.AIAccumulatorPricer(optionPremiumPVRequest, observeScheduleArray, tradeVolSurface);
        resultVo.setOptionPremium(BigDecimal.valueOf(tradeAiAccumulatorPricerResult.getPv())
                .setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(-1)));
        resultVo.setOptionPremiumPercent((resultVo.getOptionPremium()
                .divide(quoteDto.getEntryPrice(), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));
        //计算成交金额
        resultVo.setTotalAmount(resultVo.getOptionPremium());
        resultVo.setDay1PnL(resultVo.getTotalAmount().add(resultVo.getPv()).setScale(4, RoundingMode.HALF_UP));


    }

    /**
     * 熔断累计期权计算
     * @param resultVo             计算结果
     * @param quoteDto             定价计算明细
     * @param riskFreeInterestRate 无风险利率
     * @param marginRate           保证金系数
     * @param evaluationTime       计算时间
     * @param underlying           合约详情
     */
    private void koAccumulatorPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto, BigDecimal riskFreeInterestRate
            , BigDecimal marginRate, long evaluationTime, UnderlyingManagerVO underlying,OpenOrCloseEnum openOrClose,TradeMng tradeMng) {
        ObserveSchedule[] observeScheduleArray = getObserveScheduleArray(quoteDto.getTradeObsDateList());

        String accumulatorType = getAccumulatorType(quoteDto.getOptionType());

        BussinessException.E_300102.assertNotNull(quoteDto.getBasicQuantity(), "每日价格不能为空");
        BussinessException.E_300102.assertNotNull(quoteDto.getSettleType(), "结算方式不能为空");
        //如果敲出赔付没传则用0
        if (Objects.isNull(quoteDto.getKnockoutRebate())) {
            quoteDto.setKnockoutRebate(BigDecimal.ZERO);
        }
        //如果没有敲出价格传则用0
        BussinessException.E_300102.assertNotNull(quoteDto.getBarrier(),"熔断累计期权敲出价格不能为空");
        AIKOAccumulatorPricerRequest aiKOAccumulatorPricerRequest =
                AIKOAccumulatorPricerRequest.builder()
                        .accumulatorType(accumulatorType)
                        .valueType("a")
                        .buySell(quoteDto.getBuyOrSell() == BuyOrSellEnum.buy ? -1 : 1)
                        .basicQuantity(quoteDto.getBasicQuantity().abs().doubleValue())
                        .underlyingPrice(quoteDto.getEntryPrice().doubleValue())
                        .strike(quoteDto.getStrike().doubleValue())
                        .evaluationTime(evaluationTime)
                        .expiryTime(quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                        .constantVol(0)
                        .isCashSettled(quoteDto.getSettleType().getKey())
                        .riskFreeInterestRate(riskFreeInterestRate.doubleValue())
                        .dividendYield(underlying.getDividendYield().doubleValue())
                        .dailyLeverage(quoteDto.getLeverage().doubleValue())
                        .expiryLeverage(quoteDto.getExpireMultiple().doubleValue())
                        .fixedPayment(quoteDto.getFixedPayment() == null ? 0.0 : quoteDto.getFixedPayment().doubleValue())
                        .totalObservations(observeScheduleArray.length)
                        .barrier(quoteDto.getBarrier().doubleValue())
                        .knockoutRebate(quoteDto.getKnockoutRebate().doubleValue())
                        .build();
        //合约价格需要区分开仓平仓
        if (openOrClose == OpenOrCloseEnum.open) {
            aiKOAccumulatorPricerRequest.setEntryUnderlyingPrice(quoteDto.getEntryPrice().doubleValue());
        } else {
            aiKOAccumulatorPricerRequest.setEntryUnderlyingPrice(tradeMng.getEntryPrice().doubleValue());
        }
        VolatilityQueryDto volatilityQueryDto = new VolatilityQueryDto();
        volatilityQueryDto.setQuotationDate(quoteDto.getTradeDate());
        volatilityQueryDto.setUnderlyingCode(quoteDto.getUnderlyingCode());
        List<Volatility> volatilityList = volatilityService.getVolatility(volatilityQueryDto);
        Volatility midVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.mid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "mid没配置"));
        List<VolatityDataDto> volatityDataList;
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            Volatility bidVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.bid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "bid没配置"));
            volatityDataList = getValatityDataByOffset(midVolatility.getData(), bidVolatility.getData());
        } else {
            Volatility askVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.ask).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "ask没配置"));
            volatityDataList = getValatityDataByOffset(midVolatility.getData(), askVolatility.getData());
        }
        VolSurface midVolSurface = VolatilityUtil.getVolSurface(midVolatility.getData());

//        List<CompletableFuture<AIKOAccumulatorPricerResult>> futureAll = new ArrayList<>();
//        futureAll.add(CompletableFuture.supplyAsync(() -> {
//            log.trace("线程：completableFuture:{}", Thread.currentThread().getName());
//            return jniUtil.AIKOAccumulatorPricer(aiKOAccumulatorPricerRequest, observeScheduleArray, midVolSurface);
//        }, asyncTaskExecutor));
        //计算保证金
        AIKOAccumulatorPricerResult pricerResult;
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            //客户买入累计累沽期权
            //（包括固定赔付累沽和熔断累沽、熔断固定赔付累沽）->标的涨停
            if (quoteDto.getOptionType() == OptionTypeEnum.AIPutKOAccPricer
                    || quoteDto.getOptionType() == OptionTypeEnum.AIPutFixKOAccPricer
                    || quoteDto.getOptionType() == OptionTypeEnum.AIEnPutKOAccPricer) {
                AIKOAccumulatorPricerRequest upPricePVRequest = CglibUtil.copy(aiKOAccumulatorPricerRequest, AIKOAccumulatorPricerRequest.class);
                BigDecimal upPrice = underlying.getUpDownLimit().add(BigDecimal.ONE).multiply(quoteDto.getEntryPrice());
                upPricePVRequest.setUnderlyingPrice(upPrice.doubleValue());
                upPricePVRequest.setValueType("p");
                pricerResult = jniUtil.AIKOAccumulatorPricer(upPricePVRequest, observeScheduleArray, midVolSurface);
//                futureAll.add(CompletableFuture.supplyAsync(() -> {
//                    log.trace("线程：upPricePvFuture:{}", Thread.currentThread().getName());
//                    return jniUtil.AIKOAccumulatorPricer(upPricePVRequest, observeScheduleArray, midVolSurface);
//                }, asyncTaskExecutor));
            } else {
                AIKOAccumulatorPricerRequest downPricePVRequest = CglibUtil.copy(aiKOAccumulatorPricerRequest, AIKOAccumulatorPricerRequest.class);
                BigDecimal downPrice = BigDecimal.ONE.subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
                downPricePVRequest.setUnderlyingPrice(downPrice.doubleValue());
                downPricePVRequest.setValueType("p");
                pricerResult = jniUtil.AIKOAccumulatorPricer(downPricePVRequest, observeScheduleArray, midVolSurface);
//                futureAll.add(CompletableFuture.supplyAsync(() -> {
//                    log.trace("线程：downPricePvFuture:{}", Thread.currentThread().getName());
//                    return jniUtil.AIKOAccumulatorPricer(downPricePVRequest, observeScheduleArray, midVolSurface);
//                }, asyncTaskExecutor));
            }
        } else {
            if (quoteDto.getOptionType() == OptionTypeEnum.AICallKOAccPricer
                    || quoteDto.getOptionType() == OptionTypeEnum.AICallFixKOAccPricer
                    || quoteDto.getOptionType() == OptionTypeEnum.AIEnCallKOAccPricer) {
                AIKOAccumulatorPricerRequest upPricePVRequest = CglibUtil.copy(aiKOAccumulatorPricerRequest, AIKOAccumulatorPricerRequest.class);
                BigDecimal upPrice = underlying.getUpDownLimit().add(BigDecimal.ONE).multiply(quoteDto.getEntryPrice());
                upPricePVRequest.setUnderlyingPrice(upPrice.doubleValue());
                upPricePVRequest.setValueType("p");
                pricerResult = jniUtil.AIKOAccumulatorPricer(upPricePVRequest, observeScheduleArray, midVolSurface);
//                futureAll.add(CompletableFuture.supplyAsync(() -> {
//                    log.trace("线程：upPricePvFuture:{}", Thread.currentThread().getName());
//                    return jniUtil.AIKOAccumulatorPricer(upPricePVRequest, observeScheduleArray, midVolSurface);
//                }, asyncTaskExecutor));
            } else {
                AIKOAccumulatorPricerRequest downPricePVRequest = CglibUtil.copy(aiKOAccumulatorPricerRequest, AIKOAccumulatorPricerRequest.class);
                BigDecimal downPrice = BigDecimal.ONE.subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
                downPricePVRequest.setUnderlyingPrice(downPrice.doubleValue());
                downPricePVRequest.setValueType("p");
                pricerResult = jniUtil.AIKOAccumulatorPricer(downPricePVRequest, observeScheduleArray, midVolSurface);
//                futureAll.add(CompletableFuture.supplyAsync(() -> {
//                    log.trace("线程：downPricePvFuture:{}", Thread.currentThread().getName());
//                    return jniUtil.AIKOAccumulatorPricer(downPricePVRequest, observeScheduleArray, midVolSurface);
//                }, asyncTaskExecutor));
            }
        }


        //计算期权单价
        VolSurface tradeVolSurface = VolatilityUtil.getVolSurface(volatityDataList);
        AIKOAccumulatorPricerRequest optionPremiumRequest = CglibUtil.copy(aiKOAccumulatorPricerRequest, AIKOAccumulatorPricerRequest.class);
        optionPremiumRequest.setConstantVol(Objects.nonNull(quoteDto.getTradeVol()) ? BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue() : 0);
        optionPremiumRequest.setValueType("p");
        AIKOAccumulatorPricerResult tradeAiAccumulatorPricerResult = jniUtil.AIKOAccumulatorPricer(optionPremiumRequest, observeScheduleArray, tradeVolSurface);
//        futureAll.add( CompletableFuture.supplyAsync(() -> {
//            log.trace("线程：optionPremiumFuture:{}", Thread.currentThread().getName());
//            return jniUtil.AIKOAccumulatorPricer(optionPremiumRequest, observeScheduleArray, tradeVolSurface);
//        }, asyncTaskExecutor));
        //求保证金
        /*
          F̅=(1+α)F_0
          F_=(1-α)F_0
          α为涨跌停比例
          F_0为期初价格
          γ为保证金系数
         */
        //等待全部执行完成
        //CompletableFuture.allOf(futureAll.toArray(new CompletableFuture[0])).join();
        //获取内容
        //AIKOAccumulatorPricerResult aiKOAccumulatorPricerResult = futureAll.get(0).get();
        //AIKOAccumulatorPricerResult tradeAiAccumulatorPricerResult = futureAll.get(2).get();
        AIKOAccumulatorPricerResult aiKOAccumulatorPricerResult = jniUtil.AIKOAccumulatorPricer(aiKOAccumulatorPricerRequest, observeScheduleArray, midVolSurface);
        copySoResult(resultVo, quoteDto, underlying, aiKOAccumulatorPricerResult.getPv()
                , aiKOAccumulatorPricerResult.getGamma(), aiKOAccumulatorPricerResult.getDelta()
                , aiKOAccumulatorPricerResult.getRhoPercentage(), aiKOAccumulatorPricerResult.getDividendRhoPercentage()
                , aiKOAccumulatorPricerResult.getVegaPercentage(), aiKOAccumulatorPricerResult.getThetaPerDay());
        resultVo.setOptionPremium(BigDecimal.valueOf(tradeAiAccumulatorPricerResult.getPv()).negate().setScale(2, RoundingMode.HALF_UP));
        resultVo.setOptionPremiumPercent((resultVo.getOptionPremium().divide(quoteDto.getEntryPrice(), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));
        resultVo.setTotalAmount(resultVo.getOptionPremium());
        resultVo.setDay1PnL(resultVo.getTotalAmount().add(resultVo.getPv()).setScale(4, RoundingMode.HALF_UP));
        //涨跌停的PV
        double pricePv = pricerResult.getPv();
        BigDecimal max = BigDecimal.valueOf(pricePv).max(BigDecimal.ZERO).multiply(marginRate);
        BigDecimal margin = max.setScale(2, RoundingMode.HALF_UP);
        resultVo.setMargin(margin);

    }

    /**
     * 远期期权计算
     * @param resultVo   计算结果
     * @param quoteDto   定价计算明细
     * @param underlying 合约详情
     */
    private void forwardPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto, UnderlyingManagerVO underlying) {
        AIForwardPricerRequest aiForwardPricerRequest = new AIForwardPricerRequest(quoteDto.getEntryPrice().doubleValue(),
                quoteDto.getStrike().doubleValue()
        );
        log.debug("request={}", JSONObject.toJSONString(aiForwardPricerRequest));
        AIForwardPricerResult aiForwardPricerResult = jniUtil.AIForwardPricer(aiForwardPricerRequest);
        log.debug("request={}", JSONObject.toJSONString(aiForwardPricerResult));

        copySoResult(resultVo, quoteDto, underlying, aiForwardPricerResult.getPv()
                , aiForwardPricerResult.getGamma(), aiForwardPricerResult.getDelta()
                , aiForwardPricerResult.getRhoPercentage(), aiForwardPricerResult.getDividendRhoPercentage()
                , aiForwardPricerResult.getVegaPercentage(), aiForwardPricerResult.getThetaPerDay());

        //计算期权单价
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            resultVo.setOptionPremium(BigDecimal.valueOf(aiForwardPricerResult.getPv()).setScale(2, RoundingMode.HALF_UP));
        } else {
            resultVo.setOptionPremium(BigDecimal.valueOf(aiForwardPricerResult.getPv()).setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(-1)));
        }
        resultVo.setOptionPremiumPercent((resultVo.getOptionPremium().divide(quoteDto.getEntryPrice(), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));
        //计算成交金额
        resultVo.setTotalAmount(resultVo.getOptionPremium().multiply(quoteDto.getTradeVolume()));
        //远期day1是0
        resultVo.setDay1PnL(BigDecimal.ZERO);

        //求保证金
        /*
         * 远期： max{γmax{F_0-F ̅,F ̅-F_0 },γmax{F_0-▁F,▁F-F_0 }}=γαF_0
         */
        ClientLevelVo clientLevel = clientClient.getClientLevel(quoteDto.getClientId());
        resultVo.setMargin(clientLevel.getMarginRate().multiply(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice()).setScale(2, RoundingMode.HALF_UP));

    }

    /**
     * 雪球计算结果
     * @param resultVo             计算结果
     * @param quoteDto             定价计算明细
     * @param riskFreeInterestRate 无风险利率
     * @param evaluationTime       计算时间
     * @param underlying           合约详情
     * @param quoteType            计算类型
     */
    private void snowBallPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto
            , BigDecimal riskFreeInterestRate
            , int mcNumberPaths, int pdeTimeGrid, int pdeSpotGrid
            , long evaluationTime, UnderlyingManagerVO underlying
            , OpenOrCloseEnum quoteType, TradeMng tradeMng) {
//        BussinessException.E_300102.assertTrue(checkRelative(quoteDto), "所有价格必须都为相对或绝对");
        KnockOutSchedule[] knockOutScheduleArray = getKnockOutScheduleArray(quoteDto.getTradeObsDateList());
        //定价方法
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();
        algorithmParameters.setAlgorithmName(quoteDto.getAlgorithmName());
        algorithmParameters.setMcNumberPaths(mcNumberPaths);
        algorithmParameters.setPdeTimeGrid(pdeTimeGrid);
        algorithmParameters.setPdeSpotGrid(pdeSpotGrid);
        RateStruct returnRate;
        if (quoteDto.getReturnRateStructValue() != null) {
            returnRate = new RateStruct(BigDecimalUtil.percentageToBigDecimal(quoteDto.getReturnRateStructValue()).doubleValue(), quoteDto.getReturnRateAnnulized());
        } else {
            returnRate = new RateStruct(BigDecimalUtil.percentageToBigDecimal(quoteDto.getOptionPremiumPercent().negate()).doubleValue()
                    ,quoteDto.getOptionPremiumPercentAnnulized());
        }
        //红利票息率
        quoteDto.setBonusRateStructValue(BigDecimalUtil.percentageToBigDecimal(quoteDto.getBonusRateStructValue()));
        RateStruct bonusRate = new RateStruct(quoteDto.getBonusRateStructValue().doubleValue(), quoteDto.getBonusRateAnnulized());

        AISnowBallPricerRequest aiSnowBallPricerRequest =
                AISnowBallPricerRequest.builder()
                        .algorithmParameters(algorithmParameters)
                        .evaluationTime(evaluationTime)
                        .riskFreeInterestRate(riskFreeInterestRate.doubleValue())
                        .dividendYield(underlying.getDividendYield().doubleValue())
                        .volatility(BigDecimalUtil.percentageToBigDecimal(quoteDto.getMidVol()).doubleValue())
                        .returnRate(returnRate)
                        .optionType(getCallOrPut(quoteDto.getOptionType()))
                        .bonusRate(bonusRate)
                        .totalObservations(knockOutScheduleArray.length)
                        .productStartDate(quoteDto.getProductStartDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                        .productEndDate(quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                        .underlyingPrice(quoteDto.getEntryPrice().doubleValue())
                        .alreadyKnockedIn(quoteDto.getAlreadyKnockedIn() != null && quoteDto.getAlreadyKnockedIn())
                        .build();
        //雪球合约价格需要区分开仓平仓
        if (quoteType == OpenOrCloseEnum.open) {
            aiSnowBallPricerRequest.setEntryUnderlyingPrice(quoteDto.getEntryPrice().doubleValue());
        } else {
            aiSnowBallPricerRequest.setEntryUnderlyingPrice(tradeMng.getEntryPrice().doubleValue());
        }

        Level defaultLeve = new Level();
        //敲入价格
        if (quoteDto.getKnockinBarrierValue() != null && quoteDto.getKnockinBarrierRelative() != null) {
            if (quoteDto.getKnockinBarrierRelative()) {
                quoteDto.setKnockinBarrierValue(BigDecimalUtil.percentageToBigDecimal(quoteDto.getKnockinBarrierValue()));
            }
            Level knockinBarrier = new Level(quoteDto.getKnockinBarrierValue().doubleValue(), quoteDto.getKnockinBarrierRelative(),
                    quoteDto.getKnockinBarrierShift() == null ? 0 : quoteDto.getKnockinBarrierShift().doubleValue());
            aiSnowBallPricerRequest.setKnockinBarrier(knockinBarrier);
        } else {
            aiSnowBallPricerRequest.setKnockinBarrier(defaultLeve);
        }
        //敲入行权价格
        if (quoteDto.getStrikeOnceKnockedinRelative() != null && quoteDto.getStrikeOnceKnockedinValue() != null) {
            if (quoteDto.getStrikeOnceKnockedinRelative()) {
                quoteDto.setStrikeOnceKnockedinValue(BigDecimalUtil.percentageToBigDecimal(quoteDto.getStrikeOnceKnockedinValue()));
            }
            Level strikeOnceKnockedin = new Level(quoteDto.getStrikeOnceKnockedinValue().doubleValue(), quoteDto.getStrikeOnceKnockedinRelative()
                    , quoteDto.getStrikeOnceKnockedinShift() == null ? 0 : quoteDto.getStrikeOnceKnockedinShift().doubleValue());
            aiSnowBallPricerRequest.setStrikeOnceKnockedin(strikeOnceKnockedin);
        } else {
            aiSnowBallPricerRequest.setStrikeOnceKnockedin(defaultLeve);
        }
        //敲入行权价格2
        if (quoteDto.getStrike2OnceKnockedinValue() != null && quoteDto.getStrike2OnceKnockedinRelative() != null) {
            if (quoteDto.getStrike2OnceKnockedinRelative()) {
                quoteDto.setStrike2OnceKnockedinValue(BigDecimalUtil.percentageToBigDecimal(quoteDto.getStrike2OnceKnockedinValue()));
            }
            Level strike2OnceKnockedin = new Level(quoteDto.getStrike2OnceKnockedinValue().doubleValue(), quoteDto.getStrike2OnceKnockedinRelative()
                    , quoteDto.getStrike2OnceKnockedinShift() == null ? 0 : quoteDto.getStrike2OnceKnockedinShift().doubleValue());
            aiSnowBallPricerRequest.setStrike2OnceKnockedin(strike2OnceKnockedin);
        } else {
            if (quoteDto.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
                aiSnowBallPricerRequest.setStrike2OnceKnockedin(Level.builder().levelRelative(true).levelValue(2).build());
            } else {
                aiSnowBallPricerRequest.setStrike2OnceKnockedin(defaultLeve);
            }
        }


        AISnowBallPricerResult aiSnowBallPricerResult = jniUtil.AISnowBallPricer(aiSnowBallPricerRequest, knockOutScheduleArray);
        copySoResult(resultVo, quoteDto, underlying, aiSnowBallPricerResult.getPv()
                , aiSnowBallPricerResult.getGamma(), aiSnowBallPricerResult.getDelta()
                , aiSnowBallPricerResult.getRhoPercentage(), aiSnowBallPricerResult.getDividendRhoPercentage()
                , aiSnowBallPricerResult.getVegaPercentage(), aiSnowBallPricerResult.getThetaPerDay());
        //雪球期权开仓单价为0Day1Pnl=Pv
        if (quoteType == OpenOrCloseEnum.open) {
            resultVo.setOptionPremium(BigDecimal.ZERO);
            resultVo.setDay1PnL(BigDecimal.valueOf(aiSnowBallPricerResult.getPv()));
        } else {
            //平仓波动率
            aiSnowBallPricerRequest.setVolatility(BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue());
            //平仓分红率
            aiSnowBallPricerRequest.setDividendYield(BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeDividendYield()).doubleValue());
            AISnowBallPricerResult tradePvResult = jniUtil.AISnowBallPricer(aiSnowBallPricerRequest, knockOutScheduleArray);
            resultVo.setOptionPremium(BigDecimal.valueOf(tradePvResult.getPv()));
            resultVo.setDay1PnL(BigDecimal.valueOf(tradePvResult.getPv() + aiSnowBallPricerResult.getPv()));
        }


    }

    /**
     * 保险亚式计算结果
     * @param resultVo             计算结果
     * @param quoteDto             定价计算明细
     * @param riskFreeInterestRate 无风险利率
     * @param evaluationTime       计算时间
     * @param marginRate           保证金系数
     * @param underlying           合约详情
     */
    private void insuranceAsianPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto
            , BigDecimal riskFreeInterestRate,BigDecimal marginRate, long evaluationTime, UnderlyingManagerVO underlying) {
        ObserveSchedule[] observeSchedules = getObserveScheduleArray(quoteDto.getTradeObsDateList());
        AIInsuranceAsianPricerRequest insuranceAsianPricerRequest = AIInsuranceAsianPricerRequest.builder()
                .callPut(quoteDto.getCallOrPut().name())
                .ceilFloor(quoteDto.getCeilFloor().name())
                .underlyingPrice(quoteDto.getEntryPrice().doubleValue())
                .strike1(quoteDto.getStrike().doubleValue())
                .strike2(quoteDto.getStrike2().doubleValue())
                .discountRate(BigDecimalUtil.percentageToBigDecimal(quoteDto.getDiscountRate()).doubleValue())
                .evaluationTime(evaluationTime)
                .expiryTime(quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                .constantVol(Objects.nonNull(quoteDto.getTradeVol()) ? BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue() : 0)
                .totalObservations(observeSchedules.length)
                .riskFreeInterestRate(riskFreeInterestRate.doubleValue())
                .dividendRate(riskFreeInterestRate.doubleValue())
                .pathNumber(systemConfigUtil.getPathNumber())
                .threadNumber(systemConfigUtil.getThreadNumber())
                .build();

        VolatilityQueryDto volatilityQueryDto = new VolatilityQueryDto();
        volatilityQueryDto.setQuotationDate(quoteDto.getTradeDate());
        volatilityQueryDto.setUnderlyingCode(quoteDto.getUnderlyingCode());
        List<Volatility> volatilityList = volatilityService.getVolatility(volatilityQueryDto);
        Volatility midVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.mid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "mid没配置"));
        List<VolatityDataDto> volatityDataList;
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            Volatility bidVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.bid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "bid没配置"));
            volatityDataList = getValatityDataByOffset(midVolatility.getData(), bidVolatility.getData());
        } else {
            Volatility askVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.ask).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "ask没配置"));
            volatityDataList = getValatityDataByOffset(midVolatility.getData(), askVolatility.getData());
        }
        VolSurface midVolSurface = VolatilityUtil.getVolSurface(midVolatility.getData());
        //计算希腊字母使用midVol
        AIInsuranceAsianPricerResult aiInsuranceAsianPricerResult = jniUtil.AIInsuranceAsianPricer(insuranceAsianPricerRequest, observeSchedules,midVolSurface);
        copySoResult(resultVo, quoteDto, underlying, aiInsuranceAsianPricerResult.getPv()
                , aiInsuranceAsianPricerResult.getGamma(), aiInsuranceAsianPricerResult.getDelta()
                , aiInsuranceAsianPricerResult.getRhoPercentage(), aiInsuranceAsianPricerResult.getDividendRhoPercentage()
                , aiInsuranceAsianPricerResult.getVegaPercentage(), aiInsuranceAsianPricerResult.getThetaPerDay());
        //计算期权单价使用成交波动率
        insuranceAsianPricerRequest.setConstantVol(Objects.nonNull(quoteDto.getTradeVol()) ? BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue() : 0);
        AIInsuranceAsianPricerResult tradeAIInsuranceAsianPricerResult = jniUtil.AIInsuranceAsianPricer(insuranceAsianPricerRequest, observeSchedules, VolatilityUtil.getVolSurface(volatityDataList));
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
            resultVo.setOptionPremium(BigDecimal.valueOf(tradeAIInsuranceAsianPricerResult.getPv()).setScale(2, RoundingMode.HALF_UP));
        } else {
            resultVo.setOptionPremium(BigDecimal.valueOf(tradeAIInsuranceAsianPricerResult.getPv()).setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(-1)));
        }
        resultVo.setOptionPremiumPercent((resultVo.getOptionPremium().divide(quoteDto.getEntryPrice(), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));
        //计算成交金额
        resultVo.setTotalAmount(resultVo.getOptionPremium());
        resultVo.setDay1PnL(resultVo.getTotalAmount().add(resultVo.getPv()).setScale(4, RoundingMode.HALF_UP));

        //求保证金
        /*
         * max{(γmax{-PV(F_0 )+PV(F̅ ),0},γmax{-PV(F_0 )+PV(F_),0} )}
         * F̅=(1+α)F_0
         * F_=(1-α)F_0
         * α为涨跌停比例
         * F_0为期初价格
         * γ为保证金系数
         */
        if (quoteDto.getBuyOrSell() == BuyOrSellEnum.sell) {
            BigDecimal upPrice = underlying.getUpDownLimit().add(BigDecimal.valueOf(1)).multiply(quoteDto.getEntryPrice());
            BigDecimal downPrice = BigDecimal.valueOf(1).subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
            insuranceAsianPricerRequest.setUnderlyingPrice(upPrice.doubleValue());
            double upPricePv = jniUtil.AIInsuranceAsianPricer(insuranceAsianPricerRequest, observeSchedules, midVolSurface).getPv();
            insuranceAsianPricerRequest.setUnderlyingPrice(downPrice.doubleValue());
            double downPricePv = jniUtil.AIInsuranceAsianPricer(insuranceAsianPricerRequest, observeSchedules, midVolSurface).getPv();
            BigDecimal margin = marginRate.multiply(BigDecimal.valueOf(upPricePv).subtract(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getPv())).max(BigDecimal.valueOf(0)))
                    .max(marginRate.multiply(BigDecimal.valueOf(downPricePv).subtract(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getPv()))).max(BigDecimal.valueOf(0)))
                    .setScale(2, RoundingMode.HALF_UP);
            resultVo.setMargin(margin);
        } else {
            resultVo.setMargin(BigDecimal.ZERO);
        }
    }
    /**
     * 折价雪球计算结果
     * @param resultVo             计算结果
     * @param quoteDto             定价计算明细
     * @param riskFreeInterestRate 无风险利率
     * @param evaluationTime       计算时间
     * @param underlying           合约详情
     * @param quoteType            计算类型
     */
    private void disOpenSnowBallPricer(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto
            , BigDecimal riskFreeInterestRate
            , int mcNumberPaths, int pdeTimeGrid, int pdeSpotGrid
            , long evaluationTime, UnderlyingManagerVO underlying
            , OpenOrCloseEnum quoteType, TradeMng tradeMng) {
        KnockOutSchedule[] knockOutScheduleArray = getKnockOutScheduleArray(quoteDto.getTradeObsDateList());

        //定价方法
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();
        algorithmParameters.setAlgorithmName(quoteDto.getAlgorithmName());
        algorithmParameters.setMcNumberPaths(mcNumberPaths);
        algorithmParameters.setPdeTimeGrid(pdeTimeGrid);
        algorithmParameters.setPdeSpotGrid(pdeSpotGrid);
        RateStruct returnRate;
        if (quoteDto.getReturnRateStructValue() != null) {
            returnRate = new RateStruct(BigDecimalUtil.percentageToBigDecimal(quoteDto.getReturnRateStructValue()).doubleValue(), quoteDto.getReturnRateAnnulized());
        } else {
            returnRate = new RateStruct(BigDecimalUtil.percentageToBigDecimal(quoteDto.getOptionPremiumPercent().negate()).doubleValue()
                    ,quoteDto.getOptionPremiumPercentAnnulized());
        }
        //红利票息率
        quoteDto.setBonusRateStructValue(BigDecimalUtil.percentageToBigDecimal(quoteDto.getBonusRateStructValue()));
        RateStruct bonusRate = new RateStruct(quoteDto.getBonusRateStructValue().doubleValue(), quoteDto.getBonusRateAnnulized());

        AIDisOpenSnowBallPricerRequest aiSnowBallPricerRequest =
                AIDisOpenSnowBallPricerRequest.builder()
                        .algorithmParameters(algorithmParameters)
                        .evaluationTime(evaluationTime)
                        .riskFreeInterestRate(riskFreeInterestRate.doubleValue())
                        .dividendYield(underlying.getDividendYield().doubleValue())
                        .returnRate(returnRate)
                        .optionType(getCallOrPut(quoteDto.getOptionType()))
                        .bonusRate(bonusRate)
                        .totalObservations(knockOutScheduleArray.length)
                        .productStartDate(quoteDto.getProductStartDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                        .productEndDate(quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                        .underlyingPrice(quoteDto.getEntryPrice().doubleValue())
                        .isEquity(quoteDto.getIsEquity())
                        .isDividendConstant(quoteDto.getIsDividendConstant())
                        .constantVol(Objects.nonNull(quoteDto.getTradeVol()) ? BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeVol()).doubleValue() : 0)
                        .isKnockedInEnd(quoteDto.getIsKnockedInEnd())
                        .isAlreadyKnockedIn(quoteDto.getIsAlreadyKnockedIn())
                        .participationRatio(quoteDto.getParticipationRatio())
                        .isCashSettled(quoteDto.getIsCashSettled())
                        .isSpotOpen(quoteDto.getIsSpotOpen())
                        .build();
        //雪球合约价格需要区分开仓平仓
        if (quoteType == OpenOrCloseEnum.open) {
            aiSnowBallPricerRequest.setEntryUnderlyingPrice(quoteDto.getEntryPrice().doubleValue());
        } else {
            aiSnowBallPricerRequest.setEntryUnderlyingPrice(tradeMng.getEntryPrice().doubleValue());
        }

        Level defaultLeve = new Level();
        //敲入价格
        if (quoteDto.getKnockinBarrierValue() != null && quoteDto.getKnockinBarrierRelative() != null) {
            if (quoteDto.getKnockinBarrierRelative()) {
                quoteDto.setKnockinBarrierValue(BigDecimalUtil.percentageToBigDecimal(quoteDto.getKnockinBarrierValue()));
            }
            Level knockinBarrier = new Level(quoteDto.getKnockinBarrierValue().doubleValue(), quoteDto.getKnockinBarrierRelative(),
                    quoteDto.getKnockinBarrierShift() == null ? 0 : quoteDto.getKnockinBarrierShift().doubleValue());
            aiSnowBallPricerRequest.setKnockinBarrier(knockinBarrier);
        } else {
            aiSnowBallPricerRequest.setKnockinBarrier(defaultLeve);
        }
        //敲入行权价格
        if (quoteDto.getStrikeOnceKnockedinRelative() != null && quoteDto.getStrikeOnceKnockedinValue() != null) {
            if (quoteDto.getStrikeOnceKnockedinRelative()) {
                quoteDto.setStrikeOnceKnockedinValue(BigDecimalUtil.percentageToBigDecimal(quoteDto.getStrikeOnceKnockedinValue()));
            }
            Level strikeOnceKnockedin = new Level(quoteDto.getStrikeOnceKnockedinValue().doubleValue(), quoteDto.getStrikeOnceKnockedinRelative()
                    , quoteDto.getStrikeOnceKnockedinShift() == null ? 0 : quoteDto.getStrikeOnceKnockedinShift().doubleValue());
            aiSnowBallPricerRequest.setStrikeOnceKnockedin(strikeOnceKnockedin);
        } else {
            aiSnowBallPricerRequest.setStrikeOnceKnockedin(defaultLeve);
        }

        VolatilityQueryDto volatilityQueryDto = new VolatilityQueryDto();
        volatilityQueryDto.setQuotationDate(quoteDto.getTradeDate());
        volatilityQueryDto.setUnderlyingCode(quoteDto.getUnderlyingCode());

        List<Volatility> volatilityList = volatilityService.getVolatility(volatilityQueryDto);
        Volatility midVolatility = volatilityList.stream().filter(vol -> vol.getVolType() == VolTypeEnum.mid).findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300103, "mid没配置"));
        VolSurface volSurface = VolatilityUtil.getVolSurface(midVolatility.getData());

        DivTermStructure divTermStructure = new DivTermStructure();

        AIDisOpenSnowBallPricerResult aiDisOpenSnowBallPricerResult = jniUtil.AIDisOpenSnowBallPricer(aiSnowBallPricerRequest, knockOutScheduleArray,volSurface,divTermStructure);
        copySoResult(resultVo, quoteDto, underlying, aiDisOpenSnowBallPricerResult.getPv()
                , aiDisOpenSnowBallPricerResult.getGamma(), aiDisOpenSnowBallPricerResult.getDelta()
                , aiDisOpenSnowBallPricerResult.getRhoPercentage(), aiDisOpenSnowBallPricerResult.getDividendRhoPercentage()
                , aiDisOpenSnowBallPricerResult.getVegaPercentage(), aiDisOpenSnowBallPricerResult.getThetaPerDay());
        //雪球期权开仓单价为0Day1Pnl=Pv
        if (quoteType == OpenOrCloseEnum.open) {
            resultVo.setOptionPremium(BigDecimal.ZERO);
            resultVo.setDay1PnL(BigDecimal.valueOf(aiDisOpenSnowBallPricerResult.getPv()));
        } else {
            //平仓分红率
            aiSnowBallPricerRequest.setDividendYield(BigDecimalUtil.percentageToBigDecimal(quoteDto.getTradeDividendYield()).doubleValue());
            AIDisOpenSnowBallPricerResult tradePvResult = jniUtil.AIDisOpenSnowBallPricer(aiSnowBallPricerRequest, knockOutScheduleArray,volSurface,divTermStructure);
            resultVo.setOptionPremium(BigDecimal.valueOf(tradePvResult.getPv()));
            resultVo.setDay1PnL(BigDecimal.valueOf(tradePvResult.getPv() + aiDisOpenSnowBallPricerResult.getPv()));
        }
    }
    private KnockOutSchedule[] getKnockOutScheduleArray(List<TradeObsDateDto> tradeObsDateList) {
        List<KnockOutSchedule> knockOutScheduleList = new ArrayList<>();
        BussinessException.E_300101.assertTrue(Objects.nonNull(tradeObsDateList) && !tradeObsDateList.isEmpty(), "观察日期不能为空");
        for (TradeObsDateDto dto : tradeObsDateList) {
            KnockOutSchedule observeSchedule = new KnockOutSchedule();
            observeSchedule.setBarrierRelative(dto.getBarrierRelative());
            observeSchedule.setRebateRateAnnulized(dto.getRebateRateAnnulized());
            observeSchedule.setObserveDate(dto.getObsDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
            //障碍部分赋值
            if (Objects.nonNull(dto.getBarrierRelative())) {
                observeSchedule.setBarrierRelative(dto.getBarrierRelative());
                if (dto.getBarrierRelative()) {
                    dto.setBarrier(BigDecimalUtil.percentageToBigDecimal(dto.getBarrier()));
                }
                observeSchedule.setBarrier(dto.getBarrier().doubleValue());
            }
            //票息部分赋值
            if (Objects.nonNull(dto.getRebateRateAnnulized())) {
                observeSchedule.setRebateRateAnnulized(dto.getRebateRateAnnulized());
                if (dto.getRebateRateAnnulized()) {
                    dto.setRebateRate(BigDecimalUtil.percentageToBigDecimal(dto.getRebateRate()));
                }
                observeSchedule.setRebateRate(dto.getRebateRate().doubleValue());
            }
            if (Objects.nonNull(dto.getPrice())) {
                observeSchedule.setFixedPrice(dto.getPrice().doubleValue());
            }
            if (Objects.nonNull(dto.getBarrierShift())) {
                observeSchedule.setBarrierShift(dto.getBarrierShift().doubleValue());
            }
            knockOutScheduleList.add(observeSchedule);
        }
        return knockOutScheduleList.toArray(new KnockOutSchedule[0]);
    }

    private ObserveSchedule[] getObserveScheduleArray(List<TradeObsDateDto> tradeObsDateList) {
        List<ObserveSchedule> observeScheduleList = new ArrayList<>();
        BussinessException.E_300101.assertTrue(Objects.nonNull(tradeObsDateList) && !tradeObsDateList.isEmpty(), "观察日期不能为空");
        for (TradeObsDateDto tradeObsDateDto : tradeObsDateList) {
            ObserveSchedule observeSchedule = new ObserveSchedule();
            observeSchedule.setObserveDate(tradeObsDateDto.getObsDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
            if (Objects.nonNull(tradeObsDateDto.getPrice())) {
                observeSchedule.setFixedPrice(tradeObsDateDto.getPrice().doubleValue());
            } else {
                observeSchedule.setFixedPrice(0);
            }
            observeScheduleList.add(observeSchedule);
        }
        return observeScheduleList.toArray(new ObserveSchedule[0]);
    }

    private void copySoResult(QuoteResultVo resultVo, QuoteCalculateDetailDTO quoteDto, UnderlyingManagerVO underlying, double pv
            , double gamma, double delta, double rho, double dividendRho, double vega, double theta) {
        QuoteSoResultVO quoteSoResultVO = new QuoteSoResultVO();
        quoteSoResultVO.setPv(BigDecimal.valueOf(pv));
        quoteSoResultVO.setGamma(BigDecimal.valueOf(gamma));
        quoteSoResultVO.setDelta(BigDecimal.valueOf(delta));
        quoteSoResultVO.setRho(BigDecimal.valueOf(rho));
        quoteSoResultVO.setDividendRho(BigDecimal.valueOf(dividendRho));
        quoteSoResultVO.setVega(BigDecimal.valueOf(vega));
        quoteSoResultVO.setTheta(BigDecimal.valueOf(theta));
        handleSoResult(quoteSoResultVO, resultVo, quoteDto, underlying.getContractSize());
    }

    private void handleSoResult(QuoteSoResultVO quoteSoResultVO, QuoteResultVo resultVo, @NonNull QuoteCalculateDetailDTO quoteDto, Integer contractSize) {
        switch (quoteDto.getOptionType()) {
            //累计期权
            case AICallAccPricer:
            case AIPutAccPricer:
            case AICallFixAccPricer:
            case AIPutFixAccPricer:
            case AICallKOAccPricer:
            case AIPutKOAccPricer:
            case AICallFixKOAccPricer:
            case AIPutFixKOAccPricer:
            case AIEnCallKOAccPricer:
            case AIEnPutKOAccPricer:
                resultVo.setPv(quoteSoResultVO.getPv().setScale(2, RoundingMode.HALF_UP));
                resultVo.setDelta(quoteSoResultVO.getDelta().divide(BigDecimal.valueOf(contractSize), 4, RoundingMode.HALF_UP));
                resultVo.setGamma(quoteSoResultVO.getGamma().multiply(quoteDto.getEntryPrice().multiply(quoteDto.getEntryPrice())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                resultVo.setVega(quoteSoResultVO.getVega().setScale(4, RoundingMode.HALF_UP));
                resultVo.setTheta(quoteSoResultVO.getTheta().setScale(4, RoundingMode.HALF_UP));
                resultVo.setRho(quoteSoResultVO.getRho().setScale(4, RoundingMode.HALF_UP));
                resultVo.setDividendRho(quoteSoResultVO.getDividendRho());
                break;
            //雪球期权
            case AILimitLossesSnowBallCallPricer:
            case AILimitLossesSnowBallPutPricer:
                BigDecimal strike = quoteDto.getStrikeOnceKnockedinValue();
                if (!quoteDto.getStrikeOnceKnockedinRelative()) {
                    strike = quoteDto.getStrikeOnceKnockedinValue()
                            .divide(quoteDto.getEntryPrice(), 16, RoundingMode.HALF_UP);

                }
                BigDecimal strike2 = quoteDto.getStrike2OnceKnockedinValue();
                if (!quoteDto.getStrike2OnceKnockedinRelative()) {
                    strike2 = quoteDto.getStrike2OnceKnockedinValue()
                            .divide(quoteDto.getEntryPrice(), 16, RoundingMode.HALF_UP);

                }
                //保证金占用转换为相对值取差
                resultVo.setUseMargin(strike2.subtract(strike).abs());
            case AISnowBallCallPricer:
            case AISnowBallPutPricer:
            case AIBreakEvenSnowBallCallPricer:
            case AIBreakEvenSnowBallPutPricer:
                resultVo.setPv(quoteSoResultVO.getPv());
                resultVo.setDelta(quoteSoResultVO.getDelta());
                resultVo.setGamma(quoteSoResultVO.getGamma());
                resultVo.setVega(quoteSoResultVO.getVega());
                resultVo.setTheta(quoteSoResultVO.getTheta());
                resultVo.setRho(quoteSoResultVO.getRho());
                resultVo.setDividendRho(quoteSoResultVO.getDividendRho());
                if (quoteDto.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                        quoteDto.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
                    resultVo.setUseMargin(BigDecimal.ONE);
                }
                if (Objects.isNull(resultVo.getUseMargin())) {
                    resultVo.setUseMargin(BigDecimal.ZERO);
                }
                break;
            default:
                BigDecimal pv = quoteSoResultVO.getPv().setScale(2, RoundingMode.HALF_UP).multiply(quoteDto.getTradeVolume()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal delta = quoteSoResultVO.getDelta().multiply(quoteDto.getTradeVolume()).divide(BigDecimal.valueOf(contractSize), 4, RoundingMode.HALF_UP);
                BigDecimal gamma = quoteSoResultVO.getGamma().multiply(quoteDto.getEntryPrice().multiply(quoteDto.getEntryPrice())).multiply(quoteDto.getTradeVolume()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal vega = quoteSoResultVO.getVega().multiply(quoteDto.getTradeVolume()).setScale(4, RoundingMode.HALF_UP);
                BigDecimal theta = quoteSoResultVO.getTheta().multiply(quoteDto.getTradeVolume()).setScale(4, RoundingMode.HALF_UP);
                BigDecimal rho = quoteSoResultVO.getRho().multiply(quoteDto.getTradeVolume()).setScale(4, RoundingMode.HALF_UP);
                BigDecimal dividendRho = quoteSoResultVO.getDividendRho().multiply(quoteDto.getTradeVolume()).setScale(4, RoundingMode.HALF_UP);
                if (quoteDto.getBuyOrSell() == BuyOrSellEnum.buy) {
                    resultVo.setPv(pv.negate());
                    resultVo.setDelta(delta.negate());
                    resultVo.setGamma(gamma.negate());
                    resultVo.setVega(vega.negate());
                    resultVo.setTheta(theta.negate());
                    resultVo.setRho(rho.negate());
                    resultVo.setDividendRho(dividendRho.negate());
                } else {
                    resultVo.setPv(pv);
                    resultVo.setDelta(delta);
                    resultVo.setGamma(gamma);
                    resultVo.setVega(vega);
                    resultVo.setTheta(theta);
                    resultVo.setRho(rho);
                    resultVo.setDividendRho(dividendRho);
                }
                break;
        }
    }

    private void makeUpCheck(QuoteCalculateDTO quoteCalculateDTO) {
        if (quoteCalculateDTO.getOpenOrClose() == OpenOrCloseEnum.open) {
            List<QuoteCalculateDetailDTO> quoteList = quoteCalculateDTO.getQuoteList();
            switch (quoteCalculateDTO.getOptionCombType()) {
                case bullMarketSpread:
                    BussinessException.E_300102.assertTrue(quoteList.size() == 2 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1,
                            "牛市价差看涨看跌或客户买入卖出选择错误");
                    break;
                case bearMarketSpread:
                    BussinessException.E_300102.assertTrue(quoteList.size() == 2 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1,
                            "熊市价差看涨看跌或客户买入卖出选择错误");
                    break;
                case collarSpread:
                    BussinessException.E_300102.assertTrue(quoteList.size() == 2 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1,
                            "领式结构看涨看跌或客户买入卖出选择错误");
                    break;
                case straddle:
                    BussinessException.E_300102.assertTrue(quoteList.size() == 2 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put).count() == 1 &&
                                    (quoteList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 2 ||
                                            quoteList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 2),
                            "跨式结构看涨看跌或客户买入卖出选择错误");
                case wideStrangle:
                    BussinessException.E_300102.assertTrue(quoteList.size() == 2 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put).count() == 1 &&
                                    (quoteList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 2 ||
                                            quoteList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 2),
                            "宽跨式结构看涨看跌或客户买入卖出选择错误");
                    break;
                case callTriCollar:
                    BussinessException.E_300102.assertTrue(quoteList.size() == 3 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1,
                            "看涨海鸥看涨看跌或客户买入卖出选择错误");
                    break;
                case putTriCollar:
                    BussinessException.E_300102.assertTrue(quoteList.size() == 3 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1,
                            "看跌海鸥看涨看跌或客户买入卖出选择错误");
                    break;
                case butterflySpread:
                    BussinessException.E_300102.assertTrue(quoteList.size() == 4 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 &&
                                    quoteList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1,
                            "蝶式结构看涨看跌或客户买入卖出选择错误");
                    break;
                default:
                    BussinessException.E_300102.doThrow("期权组合类型错误", quoteCalculateDTO.getOptionCombType());
            }
        }
    }

    /**
     * 计算组合保证金
     * @return 保证金
     */
    private BigDecimal getMargin(QuoteCalculateDTO quoteCalculateDTO, BigDecimal dividendYield, BigDecimal riskFreeInterestRate, BigDecimal marginRate) {
        BigDecimal sumW = BigDecimal.ZERO;
        BigDecimal upSumW = BigDecimal.ZERO;
        BigDecimal downSumW = BigDecimal.ZERO;
        for (QuoteCalculateDetailDTO quoteDto : quoteCalculateDTO.getQuoteList()) {
            long evaluationTime = LocalDateTime.of(quoteDto.getTradeDate(), LocalTime.now()).toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
            //获取计算时间
            if (Objects.nonNull(quoteDto.getTradeTime())) {
                evaluationTime = LocalDateTime.of(quoteDto.getTradeDate(), quoteDto
                        .getTradeTime()).toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
            }
            AIVanillaPricerRequest aiVanillaPricerRequestDto = new AIVanillaPricerRequest();
            aiVanillaPricerRequestDto.setOptionType(quoteDto.getCallOrPut().name());
            aiVanillaPricerRequestDto.setStrike(quoteDto.getStrike().doubleValue());
            aiVanillaPricerRequestDto.setVolatility(BigDecimalUtil.percentageToBigDecimal(quoteDto.getMidVol()).doubleValue());
            aiVanillaPricerRequestDto.setRiskFreeInterestRate(riskFreeInterestRate.doubleValue());
            aiVanillaPricerRequestDto.setDividendYield(dividendYield.doubleValue());
            aiVanillaPricerRequestDto.setUnderlyingPrice(quoteDto.getEntryPrice().doubleValue());
            aiVanillaPricerRequestDto.setEvaluationTime(evaluationTime);
            aiVanillaPricerRequestDto.setExpiryTime(quoteDto.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
            AIVanillaPricerResult aiVanillaPricerResult = jniUtil.AIVanillaPricer(aiVanillaPricerRequestDto);
            double marginPv = aiVanillaPricerResult.getPv();
            UnderlyingManagerVO underlying = underlyingManagerClient.getUnderlyingByCode(quoteDto.getUnderlyingCode());
            underlying.setUpDownLimit(BigDecimalUtil.percentageToBigDecimal(underlying.getUpDownLimit()));
            BigDecimal upMargin = BigDecimal.valueOf(1).add(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
            BigDecimal downMargin = BigDecimal.valueOf(1).subtract(underlying.getUpDownLimit()).multiply(quoteDto.getEntryPrice());
            aiVanillaPricerRequestDto.setUnderlyingPrice(upMargin.doubleValue());
            double upMarginPv = jniUtil.AIVanillaPricer(aiVanillaPricerRequestDto).getPv();
            aiVanillaPricerRequestDto.setUnderlyingPrice(downMargin.doubleValue());
            double downMarginPv = jniUtil.AIVanillaPricer(aiVanillaPricerRequestDto).getPv();
            if (quoteDto.getBuyOrSell() == BuyOrSellEnum.sell) {
                sumW = sumW.add(BigDecimal.valueOf(marginPv).negate());
                upSumW = upSumW.add(BigDecimal.valueOf(upMarginPv).negate());
                downSumW = downSumW.add(BigDecimal.valueOf(downMarginPv).negate());
            } else {
                sumW = sumW.add(BigDecimal.valueOf(marginPv));
                upSumW = upSumW.add(BigDecimal.valueOf(upMarginPv));
                downSumW = downSumW.add(BigDecimal.valueOf(downMarginPv));
            }
        }
        //如果min{W1,W2  }≥0，则保证金为0
        if (upSumW.min(downSumW).compareTo(BigDecimal.ZERO) >= 0) {
            return BigDecimal.ZERO;
        } else {
            return marginRate.multiply(sumW.subtract(upSumW).max(BigDecimal.ZERO))
                    .max(marginRate.multiply(
                            sumW.subtract(downSumW).max(BigDecimal.ZERO)
                    ));
        }
    }

    @Override
    public QuotationVO quotationList(QuotationDTO quotationDTO) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("获取日期数据");
        QuotationVO quotationVO = new QuotationVO();
        List<QuotationDataVO> quotationDataVOList = new ArrayList<>();
        LocalDate quotationDate = LocalDate.now();
        long quotationTime = LocalDate.now().atTime(LocalTime.now()).toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
        LocalDate maturityDate = quotationDate.minusDays(-29);
        if (quotationDTO.getMaturityDate() != null) {
            BussinessException.E_300102.assertTrue(systemConfigUtil.getTradeDay().isBefore(quotationDTO.getMaturityDate()), "到期日必须大于交易日");
            maturityDate = quotationDTO.getMaturityDate();
        }
        //如果传入的到期日不是交易日则取前一个交易日
        if (!calendarClient.isTradeDay(maturityDate)) {
            TradayAddDaysDto tradayAddDaysDto = new TradayAddDaysDto();
            tradayAddDaysDto.setDate(maturityDate);
            tradayAddDaysDto.setDays(-1);
            maturityDate = calendarClient.tradeDayAddDays(tradayAddDaysDto);
        }
        long maturityTime = maturityDate.atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
        quotationVO.setQuotationDate(quotationDate);
        quotationVO.setMaturityDate(maturityDate);
        stopWatch.stop();
        stopWatch.start("获取报价数据");
        List<UnderlyingQuoteVO> underlyingQuoteVOList = underlyingQuoteClient.getUnderlyingQuoteList();
        Map<Integer, UnderlyingQuoteVO> voMap = underlyingQuoteVOList.stream()
                .collect(Collectors.toMap(UnderlyingQuoteVO::getVarietyId, Function.identity(), (V1, V2) -> V2));
        List<String> underlyingCodeList = underlyingQuoteVOList.stream().filter(UnderlyingQuoteVO::getNeedQuote)
                .map(UnderlyingQuoteVO::getUnderlyingCodesList)
                .flatMap(Collection::stream).collect(Collectors.toList());
        //获取系统最新的波动率
        stopWatch.stop();
        stopWatch.start("获取系统最新的波动率");
        List<Volatility> volatilityList = volatilityService.getNewVolatility(new HashSet<>(underlyingCodeList), quotationDate);
        Map<String, List<VolatityDataDto>> midVolMap = volatilityList.stream().filter(v -> v.getVolType() == VolTypeEnum.mid)
                .collect(Collectors.toMap(Volatility::getUnderlyingCode, Volatility::getData));
        Map<String, List<VolatityDataDto>> askVolMap = volatilityList.stream().filter(v -> v.getVolType() == VolTypeEnum.ask)
                .collect(Collectors.toMap(Volatility::getUnderlyingCode, Volatility::getData));
        Map<String, List<VolatityDataDto>> bidVolMap = volatilityList.stream().filter(v -> v.getVolType() == VolTypeEnum.bid)
                .collect(Collectors.toMap(Volatility::getUnderlyingCode, Volatility::getData));
        stopWatch.stop();
        stopWatch.start("获取昨日收盘价");
        Map<String, BigDecimal> lastDayTotalMarketMap= marketClient.getCloseMarketDataByDate(systemConfigUtil.getLastTradeDay());
        stopWatch.stop();

        stopWatch.start("报价计算");
        AIVanillaPricerRequest aiVanillaPricerRequestDto = new AIVanillaPricerRequest();
        aiVanillaPricerRequestDto.setOptionType(CallOrPutEnum.call.name());
        aiVanillaPricerRequestDto.setStrike(1);
        aiVanillaPricerRequestDto.setRiskFreeInterestRate(BigDecimalUtil.percentageToBigDecimal(systemConfigUtil.getRiskFreeInterestRate()).doubleValue());
        aiVanillaPricerRequestDto.setDividendYield(BigDecimalUtil.percentageToBigDecimal(systemConfigUtil.getDividendYield()).doubleValue());
        aiVanillaPricerRequestDto.setUnderlyingPrice(1);
        aiVanillaPricerRequestDto.setEvaluationTime(quotationTime);
        aiVanillaPricerRequestDto.setExpiryTime(maturityTime);
        AILinearInterpVolSurfaceRequest aiLinearInterpVolSurface = new AILinearInterpVolSurfaceRequest();
        aiLinearInterpVolSurface.setDimTenor((double) (maturityTime - quotationTime) / 86400);
        aiLinearInterpVolSurface.setDimMoneyness(1);
        for (Map.Entry<Integer, UnderlyingQuoteVO> entry : voMap.entrySet()) {
            for (String underlyingCode : entry.getValue().getUnderlyingCodesList()) {
                QuotationDataVO quotationDataVO = new QuotationDataVO();
                quotationDataVO.setSort(entry.getValue().getSort());
                quotationDataVO.setUnderlyingCode(underlyingCode);
                quotationDataVO.setVarietyName(entry.getValue().getVarietyName());
                quotationDataVO.setVarietyType(entry.getValue().getVarietyTypeName());
                quotationDataVO.setUnderlyingPrice(lastDayTotalMarketMap.get(underlyingCode));
                //客户买入
                List<VolatityDataDto> midDataList = midVolMap.get(underlyingCode);
                BussinessException.E_300103.assertTrue(midDataList != null && !midDataList.isEmpty(), underlyingCode);
                List<VolatityDataDto> volatityDataDtoList = getValatityDataByOffset(midDataList, askVolMap.get(underlyingCode));
                VolSurface volSurface = VolatilityUtil.getVolSurface(volatityDataDtoList);
                AILinearInterpVolSurfaceResult surfaceResult = jniUtil.AILinearInterpVolSurface(aiLinearInterpVolSurface, volSurface);
                quotationDataVO.setClientBuyVol(BigDecimalUtil.bigDecimalToPercentage(BigDecimal.valueOf(surfaceResult.getVolatility())));
                aiVanillaPricerRequestDto.setVolatility(surfaceResult.getVolatility());
                AIVanillaPricerResult aiVanillaPricerResult = jniUtil.AIVanillaPricer(aiVanillaPricerRequestDto);
                quotationDataVO.setClientBuyPrice(BigDecimalUtil.bigDecimalToPercentage(BigDecimal.valueOf(aiVanillaPricerResult.getPv())));
                //客户卖出
                volatityDataDtoList = getValatityDataByOffset(midVolMap.get(underlyingCode), bidVolMap.get(underlyingCode));
                volSurface = VolatilityUtil.getVolSurface(volatityDataDtoList);
                surfaceResult = jniUtil.AILinearInterpVolSurface(aiLinearInterpVolSurface, volSurface);
                quotationDataVO.setClientSellVol(BigDecimalUtil.bigDecimalToPercentage(BigDecimal.valueOf(surfaceResult.getVolatility())));
                aiVanillaPricerRequestDto.setVolatility(surfaceResult.getVolatility());
                aiVanillaPricerResult = jniUtil.AIVanillaPricer(aiVanillaPricerRequestDto);
                quotationDataVO.setClientSellPrice(BigDecimalUtil.bigDecimalToPercentage(BigDecimal.valueOf(aiVanillaPricerResult.getPv())));
                quotationDataVOList.add(quotationDataVO);
            }
        }
        stopWatch.stop();
        quotationDataVOList.sort(Comparator.comparing(QuotationDataVO::getSort).thenComparing(QuotationDataVO::getUnderlyingCode));
        log.debug("报价预览耗时:{}",stopWatch.prettyPrint(TimeUnit.MICROSECONDS));
        quotationVO.setDataList(quotationDataVOList);
        return quotationVO;
    }

    @Override
    public void quotationReport(QuotationDTO quotationDTO, HttpServletResponse response) throws IOException {
        QuotationVO quotationVO = this.quotationList(quotationDTO);
        OutputStream outputStream = response.getOutputStream();
        StringBuilder xlsxNameBuilder = new StringBuilder();
        xlsxNameBuilder.append("场外平值期权参考报价_");
        xlsxNameBuilder.append(systemConfigUtil.getTradeDay());
        response.reset();
        response.setContentType("application/ms-excel");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode(xlsxNameBuilder.append(".xlsx").toString(), "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        String qrcodePath = quotationTemplatePath+"qrcode.jpg";
        String templateFileName = quotationTemplatePath+"quotationTemplate.xlsx";
        try (ExcelWriter excelWriter = EasyExcel
                .write(outputStream)
                .inMemory(Boolean.TRUE)
                .autoCloseStream(Boolean.FALSE)
                .registerWriteHandler(new ExcelFillCellMergeStrategy(7, new int[]{1}))
                .registerWriteHandler(new CellWriteHandler() {
                    @Override
                    public void afterCellDispose(CellWriteHandlerContext context) {
                        Integer rowIndex = context.getRowIndex();
                        if (rowIndex < 7) {
                            return;
                        }
                        WriteCellData<?> cellData = context.getFirstCellData();
                        WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                        if (rowIndex % 2 == 0) {
                            writeCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                            writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                        }
                        Object preData = context.getWriteSheetHolder().getSheet().getRow(rowIndex - 1).getCell(1).getStringCellValue();
                        Object curData = context.getRow().getCell(1).getStringCellValue();
                        if (curData.equals(preData)) {
                            writeCellStyle.setBorderTop(BorderStyle.NONE);
                        }
                        cellData.setWriteCellStyle(writeCellStyle);
                    }
                })
                .withTemplate(templateFileName)
                .build()) {

            WriteSheet writeSheet = EasyExcel.writerSheet(0).build();
            Map<String, Object> map = new HashMap<>();
            map.put("quotationDate", quotationVO.getQuotationDate().format(DatePattern.NORM_DATE_FORMATTER));
            map.put("maturityDate", quotationVO.getMaturityDate().format(DatePattern.NORM_DATE_FORMATTER));
            //数据排序
            quotationVO.getDataList().sort(Comparator.comparing(QuotationDataVO::getSort));
            //数据转换为小数
            quotationVO.getDataList().forEach(item -> {
                item.setClientBuyPrice(BigDecimalUtil.percentageToBigDecimal(item.getClientBuyPrice()));
                item.setClientBuyVol(BigDecimalUtil.percentageToBigDecimal(item.getClientBuyVol()));
                item.setClientSellPrice(BigDecimalUtil.percentageToBigDecimal(item.getClientSellPrice()));
                item.setClientSellVol(BigDecimalUtil.percentageToBigDecimal(item.getClientSellVol()));
            });
            //填充方式
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(quotationVO.getDataList(), fillConfig, writeSheet);
            ImageData imageData = new ImageData();
            imageData.setImage(FileUtils.readFileToByteArray(new File(qrcodePath)));
            imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_JPEG);
            imageData.setTop(7);
            imageData.setRight(6);
            imageData.setLeft(6);
            imageData.setBottom(7);
            WriteCellData<Void> writeCellData = new WriteCellData<>();
            List<ImageData> imageDataList = new ArrayList<>();
            writeCellData.setImageDataList(imageDataList);
            imageDataList.add(imageData);
            map.put("img", writeCellData);
            WriteCellStyle imageCellStyle = writeCellData.getOrCreateStyle();
            imageCellStyle.setBorderTop(BorderStyle.MEDIUM);
            imageCellStyle.setBorderRight(BorderStyle.MEDIUM);
            imageCellStyle.setBorderBottom(BorderStyle.MEDIUM);
            writeCellData.setWriteCellStyle(imageCellStyle);
            excelWriter.fill(map, writeSheet);
        }
    }
}
