package org.orient.otc.quote.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.jni.dto.*;
import org.orient.otc.common.jni.nativeinterface.NativeCpp;
import org.orient.otc.common.jni.vo.*;
import org.orient.otc.quote.dto.jni.*;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.service.JniSerevice;
import org.orient.otc.quote.vo.jni.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class JniSereviceImpl implements JniSerevice {
    @Autowired
    private NativeCpp nativeCpp;

    @Override
    public QuoteResultVo callSo(QuoteRequestDto quoteSoDto) {
        List<SoResultVo> resultVos = new ArrayList<>();
        List<SoRequestDto> requestDtos = quoteSoDto.getRequestDtoList();
        BussinessException.E_300101.assertTrue(Objects.nonNull(requestDtos),"requestDtos");
        for(SoRequestDto requestDto : requestDtos) {
            BussinessException.E_300101.assertTrue(Objects.nonNull(requestDto.getSoType()),"sotype");
            switch (requestDto.getSoType()) {
                case AIVanillaPricer:
                    AIVanillaPricerRequestDto aiVanillaPricerRequestDto = requestDto.getAiVanillaPricerRequest();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiVanillaPricerRequestDto),"香草参数没传");
                    AIVanillaPricerRequest aiVanillaPricerRequest = new AIVanillaPricerRequest(aiVanillaPricerRequestDto.getOptionType(),
                            aiVanillaPricerRequestDto.getUnderlyingPrice(),
                            aiVanillaPricerRequestDto.getStrike(),
                            aiVanillaPricerRequestDto.getEvaluationTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond(),
                            aiVanillaPricerRequestDto.getExpiryTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond(),
                            aiVanillaPricerRequestDto.getRiskFreeInterestRate(),
                            aiVanillaPricerRequestDto.getDividendYield(),
                            aiVanillaPricerRequestDto.getVolatility()
                            );
                    AIVanillaPricerResult aiVanillaPricerResult = nativeCpp.AIVanillaPricer(aiVanillaPricerRequest);
                    AIVanillaPricerResultVo aiVanillaPricerResultVo = new AIVanillaPricerResultVo(
                            !Double.isNaN(aiVanillaPricerResult.getPv()) ? aiVanillaPricerResult.getPv() : null,
                            !Double.isNaN(aiVanillaPricerResult.getDelta()) ? aiVanillaPricerResult.getDelta() : null,
                            !Double.isNaN(aiVanillaPricerResult.getGamma()) ? aiVanillaPricerResult.getGamma() : null,
                            !Double.isNaN(aiVanillaPricerResult.getVegaPercentage()) ? aiVanillaPricerResult.getVegaPercentage() : null,
                            !Double.isNaN(aiVanillaPricerResult.getThetaPerDay()) ? aiVanillaPricerResult.getThetaPerDay() : null,
                            !Double.isNaN(aiVanillaPricerResult.getRhoPercentage()) ? aiVanillaPricerResult.getRhoPercentage() : null,
                            !Double.isNaN(aiVanillaPricerResult.getDividendRhoPercentage()) ? aiVanillaPricerResult.getDividendRhoPercentage() : null,
                            aiVanillaPricerResult.getMessage());
                    SoResultVo soResultVo = new SoResultVo();
                    soResultVo.setAiVanillaPricerResult(aiVanillaPricerResultVo);
                    soResultVo.setTradeNo(requestDto.getTradeNo());
                    soResultVo.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo);
                    break;
                case AIForwardPricer:
                    AIForwardPricerRequestDto aiForwardPricerRequestDto = requestDto.getAiForwardPricerRequest();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiForwardPricerRequestDto),"远期参数没传");
                    AIForwardPricerRequest aiForwardPricerRequest = new AIForwardPricerRequest(aiForwardPricerRequestDto.getUnderlyingPrice(),
                            aiForwardPricerRequestDto.getStrike()
                    );
                    AIForwardPricerResult aiForwardPricerResult = nativeCpp.AIForwardPricer(aiForwardPricerRequest);
                    AIForwardPricerResultVo aiForwardPricerResultVo = new AIForwardPricerResultVo(
                            !Double.isNaN(aiForwardPricerResult.getPv()) ? aiForwardPricerResult.getPv() : null,
                            !Double.isNaN(aiForwardPricerResult.getDelta()) ? aiForwardPricerResult.getDelta() : null,
                            !Double.isNaN(aiForwardPricerResult.getGamma()) ? aiForwardPricerResult.getGamma() : null,
                            !Double.isNaN(aiForwardPricerResult.getVegaPercentage()) ? aiForwardPricerResult.getVegaPercentage() : null,
                            !Double.isNaN(aiForwardPricerResult.getThetaPerDay()) ? aiForwardPricerResult.getThetaPerDay() : null,
                            !Double.isNaN(aiForwardPricerResult.getRhoPercentage()) ? aiForwardPricerResult.getRhoPercentage() : null,
                            !Double.isNaN(aiForwardPricerResult.getDividendRhoPercentage()) ? aiForwardPricerResult.getDividendRhoPercentage() : null,
                            aiForwardPricerResult.getMessage());
                    SoResultVo soResultVo1 = new SoResultVo();
                    soResultVo1.setAiForwardPricerResult(aiForwardPricerResultVo);
                    soResultVo1.setTradeNo(requestDto.getTradeNo());
                    soResultVo1.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo1);
                    break;
                case AIAsianPricer:
                    AIAsianPricerRequestDto aiAsianPricerRequestDto = requestDto.getAiAsianPricerRequest();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiAsianPricerRequestDto),"亚式参数没传");
                    long evaluationTime = aiAsianPricerRequestDto.getEvaluationTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000;
                    AIAsianPricerRequest aiAsianPricerRequest = new AIAsianPricerRequest(aiAsianPricerRequestDto.getOptionType(),
                            aiAsianPricerRequestDto.getUnderlyingPrice(),
                            evaluationTime,
                            aiAsianPricerRequestDto.getStrike(),
                            aiAsianPricerRequestDto.getExpiryTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond(),
                            aiAsianPricerRequestDto.getRiskFreeInterestRate(),
                            aiAsianPricerRequestDto.getVolatility(),
                            aiAsianPricerRequestDto.getTotalObservations());
                    List<ObserveScheduleDto> observeScheduleDtos = aiAsianPricerRequestDto.getObserveSchedule();
                    List<ObserveSchedule> observeScheduleList = new ArrayList<>();
                    for(ObserveScheduleDto observeScheduleDto : observeScheduleDtos){
                        ObserveSchedule observeSchedule = new ObserveSchedule();
                        observeSchedule.setFixedPrice(observeScheduleDto.getFixedPrice());
                        observeSchedule.setObserveDate(observeScheduleDto.getObserveDate().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000);
                        observeScheduleList.add(observeSchedule);
                    }
                    log.info("request={}",JSONObject.toJSONString(aiAsianPricerRequest));
                    log.info("observeSchedule={}",JSONObject.toJSONString(observeScheduleList));
                    AIAsianPricerResult aiAsianPricerResult = nativeCpp.AIAsianPricer(aiAsianPricerRequest,observeScheduleList.stream().toArray(ObserveSchedule[] :: new));
                    AIAsianPricerResultVo aiAsianPricerResultVo = new AIAsianPricerResultVo(
                            !Double.isNaN(aiAsianPricerResult.getPv()) ? aiAsianPricerResult.getPv() : null,
                            !Double.isNaN(aiAsianPricerResult.getDelta()) ? aiAsianPricerResult.getDelta() : null,
                            !Double.isNaN(aiAsianPricerResult.getGamma()) ? aiAsianPricerResult.getGamma() : null,
                            !Double.isNaN(aiAsianPricerResult.getVegaPercentage()) ? aiAsianPricerResult.getVegaPercentage() : null,
                            !Double.isNaN(aiAsianPricerResult.getThetaPerDay()) ? aiAsianPricerResult.getThetaPerDay() : null,
                            !Double.isNaN(aiAsianPricerResult.getRhoPercentage()) ? aiAsianPricerResult.getRhoPercentage() : null,
                            aiAsianPricerResult.getMessage());
                    SoResultVo soResultVo2 = new SoResultVo();
                    soResultVo2.setAiAsianPricerResult(aiAsianPricerResultVo);
                    soResultVo2.setTradeNo(requestDto.getTradeNo());
                    soResultVo2.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo2);
                    break;
                case AIAccumulatorPricer:
                    AIAccumulatorPricerDto aiAccumulatorPricerDto = requestDto.getAiAccumulatorPricerRequest();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiAccumulatorPricerDto),"欧式累计参数没传");
                    long l = aiAccumulatorPricerDto.getEvaluationTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000;
                    VolSurfaceDto volSurfaceDto = aiAccumulatorPricerDto.getVolSurface();
                    VolSurface volSurface = new VolSurface();
                    volSurface.setFlattenedVol(volSurfaceDto.getFlattenedVol().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface.setFlattenedVolLength(volSurfaceDto.getFlattenedVolLength());
                    volSurface.setHorizontalAxis(volSurfaceDto.getHorizontalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface.setVerticalAxis(volSurfaceDto.getVerticalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface.setHorizontalAxisLength(volSurfaceDto.getHorizontalAxisLength());
                    volSurface.setVerticalAxisLength(volSurfaceDto.getVerticalAxisLength());

                    List<ObserveScheduleDto> observeScheduleDtos1 = aiAccumulatorPricerDto.getObserveSchedule();
                    List<ObserveSchedule> observeScheduleList1 = new ArrayList<>();
                    for(ObserveScheduleDto observeScheduleDto : observeScheduleDtos1){
                        ObserveSchedule observeSchedule = new ObserveSchedule();
                        observeSchedule.setFixedPrice(observeScheduleDto.getFixedPrice());
                        observeSchedule.setObserveDate(observeScheduleDto.getObserveDate().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000);
                        observeScheduleList1.add(observeSchedule);
                    }
                    AIAccumulatorPricerRequest aiAccumulatorPricerRequest = new AIAccumulatorPricerRequest(aiAccumulatorPricerDto.getAccumulatorType().name(),
                            aiAccumulatorPricerDto.getValueType(),
                            aiAccumulatorPricerDto.getBuySell(),
                            aiAccumulatorPricerDto.getBasicQuantity(),
                            aiAccumulatorPricerDto.getUnderlyingPrice(),
                            aiAccumulatorPricerDto.getStrike(),
                            l,
                            aiAccumulatorPricerDto.getExpiryTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond(),
                            aiAccumulatorPricerDto.getConstantVol(),
                            aiAccumulatorPricerDto.getIsCashSettled(),
                            aiAccumulatorPricerDto.getRiskFreeInterestRate(),
                            aiAccumulatorPricerDto.getLeverage(),
                            aiAccumulatorPricerDto.getFixedPayment(),
                            aiAccumulatorPricerDto.getBarrier(),
                            aiAccumulatorPricerDto.getStrikeRamp(),
                            aiAccumulatorPricerDto.getBarrierRamp(),
                            aiAccumulatorPricerDto.getTotalObservations(),
                            aiAccumulatorPricerDto.getScenarioPrice());
                    log.info("request={}",JSONObject.toJSONString(aiAccumulatorPricerRequest));
                    log.info("observeSchedule={}",JSONObject.toJSONString(observeScheduleList1));
                    AIAccumulatorPricerResult aiAccumulatorPricerResult = nativeCpp.AIAccumulatorPricer(aiAccumulatorPricerRequest,observeScheduleList1.stream().toArray(ObserveSchedule[] :: new),volSurface);
                    AIAccumulatorPricerResultVo aiAccumulatorPricerResultVo = new AIAccumulatorPricerResultVo();
                    BeanUtils.copyProperties(aiAccumulatorPricerResult, aiAccumulatorPricerResultVo);
                    SoResultVo soResultVo3 = new SoResultVo();
                    soResultVo3.setAiAccumulatorPricerResult(aiAccumulatorPricerResultVo);
                    soResultVo3.setTradeNo(requestDto.getTradeNo());
                    soResultVo3.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo3);
                    break;

                case AIKOAccumulatorPricer:
                    AIKOAccumulatorPricerDto aikoAccumulatorPricerDto = requestDto.getAikoAccumulatorPricerDto();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aikoAccumulatorPricerDto),"熔断累计参数没传");
                    long l2 = aikoAccumulatorPricerDto.getEvaluationTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000;
                    VolSurfaceDto volSurfaceDto2 = aikoAccumulatorPricerDto.getVolSurface();
                    VolSurface volSurfaceKO = new VolSurface();
                    volSurfaceKO.setFlattenedVol(volSurfaceDto2.getFlattenedVol().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurfaceKO.setFlattenedVolLength(volSurfaceDto2.getFlattenedVolLength());
                    volSurfaceKO.setHorizontalAxis(volSurfaceDto2.getHorizontalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurfaceKO.setVerticalAxis(volSurfaceDto2.getVerticalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurfaceKO.setHorizontalAxisLength(volSurfaceDto2.getHorizontalAxisLength());
                    volSurfaceKO.setVerticalAxisLength(volSurfaceDto2.getVerticalAxisLength());

                    List<ObserveScheduleDto> observeScheduleDtosKO = aikoAccumulatorPricerDto.getObserveSchedule();
                    List<ObserveSchedule> observeScheduleListKO = new ArrayList<>();
                    for(ObserveScheduleDto observeScheduleDto : observeScheduleDtosKO){
                        ObserveSchedule observeSchedule = new ObserveSchedule();
                        observeSchedule.setFixedPrice(observeScheduleDto.getFixedPrice());
                        observeSchedule.setObserveDate(observeScheduleDto.getObserveDate().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000);
                        observeScheduleListKO.add(observeSchedule);
                    }
                    AIKOAccumulatorPricerRequest aiKOAccumulatorPricerRequest =
                            AIKOAccumulatorPricerRequest.builder()
                                    .accumulatorType(aikoAccumulatorPricerDto.getAccumulatorType().name())
                                    .valueType(aikoAccumulatorPricerDto.getValueType())
                                    .buySell(aikoAccumulatorPricerDto.getBuySell())
                                    .basicQuantity( aikoAccumulatorPricerDto.getBasicQuantity())
                                    .underlyingPrice( aikoAccumulatorPricerDto.getUnderlyingPrice())
                                    .strike(aikoAccumulatorPricerDto.getStrike())
                                    .evaluationTime(l2)
                                    .expiryTime(aikoAccumulatorPricerDto.getExpiryTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                                    .constantVol(aikoAccumulatorPricerDto.getConstantVol())
                                    .isCashSettled( aikoAccumulatorPricerDto.getIsCashSettled())
                                    .riskFreeInterestRate(aikoAccumulatorPricerDto.getRiskFreeInterestRate())
                                    .dividendYield(aikoAccumulatorPricerDto.getDividendYield())
                                    .dailyLeverage(aikoAccumulatorPricerDto.getDailyLeverage())
                                    .expiryLeverage(aikoAccumulatorPricerDto.getExpiryLeverage())
                                    .fixedPayment(aikoAccumulatorPricerDto.getFixedPayment())
                                    .totalObservations( aikoAccumulatorPricerDto.getTotalObservations())
                                    .barrier(aikoAccumulatorPricerDto.getBarrier())
                                    .knockoutRebate(aikoAccumulatorPricerDto.getKnockoutRebate())
                                    .build();
                    log.info("request={}",JSONObject.toJSONString(aiKOAccumulatorPricerRequest));
                    log.info("observeSchedule={}",JSONObject.toJSONString(observeScheduleListKO));
                    AIKOAccumulatorPricerResult aikoAccumulatorPricerResult = nativeCpp.AIKOAccumulatorPricer(aiKOAccumulatorPricerRequest, observeScheduleListKO.toArray(new ObserveSchedule[0]),volSurfaceKO);
                    AIKOAccumulatorPricerResultVo aikoAccumulatorPricerResultVo = new AIKOAccumulatorPricerResultVo();
                    BeanUtils.copyProperties(aikoAccumulatorPricerResult, aikoAccumulatorPricerResultVo);
                    SoResultVo soResultVoKO = new SoResultVo();
                    soResultVoKO.setAikoAccumulatorPricerResultVo(aikoAccumulatorPricerResultVo);
                    soResultVoKO.setTradeNo(requestDto.getTradeNo());
                    soResultVoKO.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVoKO);
                    break;
                case AISnowBallPricer:
                    AISnowBallPricerDto aiSnowBallPricerDto = requestDto.getAiSnowBallPricerRequest();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiSnowBallPricerDto),"雪球参数没传");
                    AlgorithmParameters algorithmParameters = new AlgorithmParameters();
                    AlgorithmParametersDto algorithmParameterDto = aiSnowBallPricerDto.getAlgorithmParameters();
                    BeanUtils.copyProperties(algorithmParameterDto,algorithmParameters);
                    algorithmParameters.setAlgorithmName(algorithmParameterDto.getAlgorithmName().name());
                    long snowBallEvaluationTime = aiSnowBallPricerDto.getEvaluationTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000;
                    RateStruct returnRate = new RateStruct();
                    BeanUtils.copyProperties(aiSnowBallPricerDto.getReturnRate(),returnRate);
                    RateStruct bonusRate = new RateStruct();
                    BeanUtils.copyProperties(aiSnowBallPricerDto.getBonusRate(),bonusRate);
                    Level lowerBarrier = new Level();
                    BeanUtils.copyProperties(aiSnowBallPricerDto.getKnockinBarrier(),lowerBarrier);
                    Level highStrikeOnceKnockedin = new Level();
                    BeanUtils.copyProperties(aiSnowBallPricerDto.getStrikeOnceKnockedin(),highStrikeOnceKnockedin);
                    Level lowerStrikeOnceKnockedin = new Level();
                    BeanUtils.copyProperties(aiSnowBallPricerDto.getStrike2OnceKnockedin(),lowerStrikeOnceKnockedin);
                    AISnowBallPricerRequest aiSnowBallPricerRequest = new AISnowBallPricerRequest(algorithmParameters,
                            aiSnowBallPricerDto.getOptionType(),
                            snowBallEvaluationTime,
                            aiSnowBallPricerDto.getUnderlyingPrice(),
                            aiSnowBallPricerDto.getRiskFreeInterestRate(),
                            aiSnowBallPricerDto.getDividendYield(),
                            aiSnowBallPricerDto.getVolatility(),
                            returnRate,
                            bonusRate,
                            aiSnowBallPricerDto.getTotalObservations(),
                            aiSnowBallPricerDto.getProductStartDate().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000,
                            aiSnowBallPricerDto.getProductEndDate().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000,
                            aiSnowBallPricerDto.getEntryUnderlyingPrice(),
                            lowerBarrier,
                            aiSnowBallPricerDto.getAlreadyKnockedIn(),
                            highStrikeOnceKnockedin,
                            lowerStrikeOnceKnockedin);
                    List<KnockOutScheduleDto> knockOutScheduleDtos = aiSnowBallPricerDto.getKnockoutSchedules();
                    List<KnockOutSchedule> knockoutSchedule = new ArrayList<>();
                    for(KnockOutScheduleDto knockOutScheduleDto : knockOutScheduleDtos){
                        KnockOutSchedule knockOutSchedule = new KnockOutSchedule();
                        knockOutSchedule.setBarrier(knockOutScheduleDto.getBarrier());
                        knockOutSchedule.setBarrierRelative(knockOutScheduleDto.getBarrierRelative());
                        knockOutSchedule.setRebateRate(knockOutScheduleDto.getRebateRate());
                        knockOutSchedule.setRebateRateAnnulized(knockOutScheduleDto.getRebateRateAnnulized());
                        knockOutSchedule.setObserveDate(knockOutScheduleDto.getObserveDate().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000);
                        knockoutSchedule.add(knockOutSchedule);
                    }
                    log.info("request={}",JSONObject.toJSONString(aiSnowBallPricerRequest));
                    log.info("knockoutSchedule={}",JSONObject.toJSONString(knockoutSchedule));
                    AISnowBallPricerResult aiSnowBallPricerResult = nativeCpp.AISnowBallPricer(aiSnowBallPricerRequest,knockoutSchedule.stream().toArray(KnockOutSchedule[] :: new));
                    AISnowBallPricerResultVo aiSnowBallPricerResultVo = new AISnowBallPricerResultVo();
                    BeanUtils.copyProperties(aiSnowBallPricerResult,aiSnowBallPricerResultVo);
                    SoResultVo soResultVo4 = new SoResultVo();
                    soResultVo4.setAiSnowBallPricerResult(aiSnowBallPricerResultVo);
                    soResultVo4.setTradeNo(requestDto.getTradeNo());
                    soResultVo4.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo4);
                    break;
                case AIBlackImpliedVol:
                    AIBlackImpliedVolRequestDto aiBlackImpliedVolRequestDto = requestDto.getAiBlackImpliedVolRequest();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiBlackImpliedVolRequestDto),"隐含波动率参数没传");
                    AIBlackImpliedVolRequest aiBlackImpliedVolRequest = new AIBlackImpliedVolRequest();
                    BeanUtils.copyProperties(aiBlackImpliedVolRequestDto,aiBlackImpliedVolRequest);
                    aiBlackImpliedVolRequest.setEvaluationTime(aiBlackImpliedVolRequestDto.getEvaluationTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
                    aiBlackImpliedVolRequest.setExpiryTime(aiBlackImpliedVolRequestDto.getExpiryTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
                    AIBlackImpliedVolResult aiBlackImpliedVolResult = nativeCpp.AIBlackImpliedVol(aiBlackImpliedVolRequest);
                    AIBlackImpliedVolResultVo aiBlackImpliedVolResultVo = new AIBlackImpliedVolResultVo();
                    BeanUtils.copyProperties(aiBlackImpliedVolResult,aiBlackImpliedVolResultVo);
                    SoResultVo soResultVo5 = new SoResultVo();
                    soResultVo5.setAiBlackImpliedVolResult(aiBlackImpliedVolResultVo);
                    soResultVo5.setTradeNo(requestDto.getTradeNo());
                    soResultVo5.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo5);
                    break;
                case AILinearInterpVolSurface:
                    AILinearInterpVolSurfaceRequestDto aiLinearInterpVolSurfaceRequestDto = requestDto.getAiLinearInterpVolSurfaceRequest();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiLinearInterpVolSurfaceRequestDto),"线性插值参数没传");
                    AILinearInterpVolSurfaceRequest aiLinearInterpVolSurfaceRequest = new AILinearInterpVolSurfaceRequest();
                    BeanUtils.copyProperties(aiLinearInterpVolSurfaceRequestDto,aiLinearInterpVolSurfaceRequest);
                    VolSurfaceDto volSurfaceDto1 = aiLinearInterpVolSurfaceRequestDto.getVolSurface();
                    VolSurface volSurface1 = new VolSurface();
                    volSurface1.setFlattenedVol(volSurfaceDto1.getFlattenedVol().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface1.setFlattenedVolLength(volSurfaceDto1.getFlattenedVolLength());
                    volSurface1.setHorizontalAxis(volSurfaceDto1.getHorizontalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface1.setVerticalAxis(volSurfaceDto1.getVerticalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface1.setHorizontalAxisLength(volSurfaceDto1.getHorizontalAxisLength());
                    volSurface1.setVerticalAxisLength(volSurfaceDto1.getVerticalAxisLength());
                    AILinearInterpVolSurfaceResult aiLinearInterpVolSurfaceResult = nativeCpp.AILinearInterpVolSurface(aiLinearInterpVolSurfaceRequest, volSurface1);
                    AILinearInterpVolSurfaceResultVo aiLinearInterpVolSurfaceResultVo = new AILinearInterpVolSurfaceResultVo();
                    BeanUtils.copyProperties(aiLinearInterpVolSurfaceResult,aiLinearInterpVolSurfaceResultVo);
                    SoResultVo soResultVo6 = new SoResultVo();
                    soResultVo6.setAiLinearInterpVolSurfaceResult(aiLinearInterpVolSurfaceResultVo);
                    soResultVo6.setTradeNo(requestDto.getTradeNo());
                    soResultVo6.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo6);
                    break;
                case AIDeltaVol2StrikeVol:
                    VolSurfaceDto aiDeltaVol2StrikeVolRequest = requestDto.getAIDeltaVol2StrikeVolRequest();
                    VolSurface volSurface2 = new VolSurface();
                    volSurface2.setFlattenedVol(aiDeltaVol2StrikeVolRequest.getFlattenedVol().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface2.setFlattenedVolLength(aiDeltaVol2StrikeVolRequest.getFlattenedVolLength());
                    volSurface2.setHorizontalAxis(aiDeltaVol2StrikeVolRequest.getHorizontalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface2.setVerticalAxis(aiDeltaVol2StrikeVolRequest.getVerticalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface2.setHorizontalAxisLength(aiDeltaVol2StrikeVolRequest.getHorizontalAxisLength());
                    volSurface2.setVerticalAxisLength(aiDeltaVol2StrikeVolRequest.getVerticalAxisLength());
                    log.info("request={}",JSONObject.toJSONString(volSurface2));
                    AIDeltaVol2StrikeVolResult aiDeltaVol2StrikeVolResult = nativeCpp.AIDeltaVol2StrikeVol(volSurface2);
                    SoResultVo soResultVo7 = new SoResultVo();
                    soResultVo7.setAIDeltaVol2StrikeVolResult(aiDeltaVol2StrikeVolResult.getVolSurface());
                    soResultVo7.setTradeNo(requestDto.getTradeNo());
                    soResultVo7.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo7);
                    break;
                case AIEnhancedAsianPricer:
                    AIEnhancedAsianPricerRequestDto aiEnhancedAsianPricerRequestDto = requestDto.getAiEnhancedAsianPricerRequestDto();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiEnhancedAsianPricerRequestDto),"增强亚式参数没传");

                    AIEnhancedAsianPricerRequest request = new AIEnhancedAsianPricerRequest();
                    request.setConstantVol(aiEnhancedAsianPricerRequestDto.getConstantVol());
                    request.setExpiryTime(aiEnhancedAsianPricerRequestDto.getExpiryTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
                    request.setStrike(aiEnhancedAsianPricerRequestDto.getStrike());
                    request.setEvaluationTime(aiEnhancedAsianPricerRequestDto.getEvaluationTime().toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
                    request.setOptionType(aiEnhancedAsianPricerRequestDto.getOptionType());
                    request.setUnderlyingPrice(aiEnhancedAsianPricerRequestDto.getUnderlyingPrice());
                    request.setRiskFreeInterestRate(aiEnhancedAsianPricerRequestDto.getRiskFreeInterestRate());
                    request.setScenarioPrice(aiEnhancedAsianPricerRequestDto.getScenarioPrice());
                    request.setValueType(aiEnhancedAsianPricerRequestDto.getValueType());
                    request.setTotalObservations(aiEnhancedAsianPricerRequestDto.getTotalObservations());
                    request.setIsCashSettled(aiEnhancedAsianPricerRequestDto.getIsCashSettled());

                    VolSurfaceDto volSurface4 = aiEnhancedAsianPricerRequestDto.getVolSurface();
                    VolSurface volSurface3 = new VolSurface();
                    volSurface3.setFlattenedVol(volSurface4.getFlattenedVol().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface3.setFlattenedVolLength(volSurface4.getFlattenedVolLength());
                    volSurface3.setHorizontalAxis(volSurface4.getHorizontalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface3.setVerticalAxis(volSurface4.getVerticalAxis().stream().mapToDouble(Double :: valueOf).toArray());
                    volSurface3.setHorizontalAxisLength(volSurface4.getHorizontalAxisLength());
                    volSurface3.setVerticalAxisLength(volSurface4.getVerticalAxisLength());

                    List<ObserveScheduleDto> observeScheduleDtos2 = aiEnhancedAsianPricerRequestDto.getObserveSchedule();
                    List<ObserveSchedule> observeScheduleList2 = new ArrayList<>();
                    for(ObserveScheduleDto observeScheduleDto : observeScheduleDtos2){
                        ObserveSchedule observeSchedule = new ObserveSchedule();
                        observeSchedule.setFixedPrice(observeScheduleDto.getFixedPrice());
                        observeSchedule.setObserveDate(observeScheduleDto.getObserveDate().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000);
                        observeScheduleList2.add(observeSchedule);
                    }
                    log.info("request={}",JSONObject.toJSONString(request));
                    log.info("observeSchedule={}",JSONObject.toJSONString(observeScheduleList2.toArray(new ObserveSchedule[0])));
                    log.info("volSurface={}",JSONObject.toJSONString(volSurface3));
                    AIEnhancedAsianPricerResult aiEnhancedAsianPricerResult = nativeCpp.AIEnhancedAsianPricer(request, observeScheduleList2.stream().toArray(ObserveSchedule[]::new), volSurface3);
                    log.info("aiEnhancedAsianPricerResult={}",JSONObject.toJSONString(aiEnhancedAsianPricerResult));
                    AIEnhancedAsianPricerResultVo aiEnhancedAsianPricerResultVo = new AIEnhancedAsianPricerResultVo();
                    BeanUtils.copyProperties(aiEnhancedAsianPricerResult, aiEnhancedAsianPricerResultVo);
                    SoResultVo soResultVo8 = new SoResultVo();
                    soResultVo8.setAiEnhancedAsianPricerResultVo(aiEnhancedAsianPricerResultVo);
                    soResultVo8.setTradeNo(requestDto.getTradeNo());
                    soResultVo8.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo8);
                    break;
                case AIInsuranceAsianPricer:
                    AIInsuranceAsianPricerRequest aiInsuranceAsianPricerRequest = requestDto.getAiInsuranceAsianPricerRequest();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiInsuranceAsianPricerRequest),"保险亚式参数aiInsuranceAsianPricerRequest-没传");
                    VolSurface aiInsuranceAsianPricerVol = requestDto.getVolSurface();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiInsuranceAsianPricerVol),"保险亚式参数aiInsuranceAsianPricerVol-没传");
                    ObserveSchedule[] aiInsuranceAsianPricerObserveScheduleList= requestDto.getObserveSchedule();
                    BussinessException.E_300101.assertTrue(Objects.nonNull(aiInsuranceAsianPricerObserveScheduleList),"保险亚式参数aiInsuranceAsianPricerObserveScheduleList-没传");
                    AIInsuranceAsianPricerResult aiInsuranceAsianPricerResult = nativeCpp.AIInsuranceAsianPricer(aiInsuranceAsianPricerRequest, aiInsuranceAsianPricerObserveScheduleList, aiInsuranceAsianPricerVol);
                    SoResultVo soResultVo9 = new SoResultVo();
                    soResultVo9.setAiInsuranceAsianPricerResult(aiInsuranceAsianPricerResult);
                    soResultVo9.setSoType(requestDto.getSoType());
                    resultVos.add(soResultVo9);
                    break;
                default:
                    BussinessException.E_300102.doThrow(requestDto.getSoType());
            }
        }

        QuoteResultVo quoteResultVo = new QuoteResultVo();
        quoteResultVo.setResultVoList(resultVos);
        return quoteResultVo;
    }
}

