package org.orient.otc.common.jni.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.common.jni.dto.*;
import org.orient.otc.common.jni.exception.BussinessException;
import org.orient.otc.common.jni.nativeinterface.NativeCpp;
import org.orient.otc.common.jni.vo.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * JNI工具类
 */
@Component
@Slf4j
public class JniUtil {
    @Resource
    NativeCpp nativeCpp;

    /**
     * 香草期权
     * @param request 计算参数
     * @return 结算结果
     */
    public  AIVanillaPricerResult AIVanillaPricer(AIVanillaPricerRequest request){
        if (log.isTraceEnabled()) {
            log.trace("AIVanillaPricerRequest={}", JSONObject.toJSONString(request));
        }
        AIVanillaPricerResult aiVanillaPricerResult = nativeCpp.AIVanillaPricer(request);
        if (log.isTraceEnabled()) {
            log.trace("AIVanillaPricerResult={}", JSONObject.toJSONString(aiVanillaPricerResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiVanillaPricerResult.getMessage()),aiVanillaPricerResult.getMessage());
        return aiVanillaPricerResult;
    }

    /**
     * 远期期权
     * @param request 计算参数
     * @return 计算结果
     */
    public  AIForwardPricerResult AIForwardPricer(AIForwardPricerRequest request){
        if (log.isTraceEnabled()) {
            log.trace("AIForwardPricerRequest={}", JSONObject.toJSONString(request));
        }
        AIForwardPricerResult aiForwardPricerResult = nativeCpp.AIForwardPricer(request);
        if (log.isTraceEnabled()) {
            log.trace("AIForwardPricerResult={}", JSONObject.toJSONString(aiForwardPricerResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiForwardPricerResult.getMessage()),aiForwardPricerResult.getMessage());
        return aiForwardPricerResult;
    }

    /**
     *  亚式期权
     * @param request 计算参数
     * @param observeSchedule 观察日列表
     * @return 计算结果
     */
    public  AIAsianPricerResult AIAsianPricer(AIAsianPricerRequest request, ObserveSchedule[] observeSchedule){
        if (log.isTraceEnabled()) {
            log.trace("AIAsianPricerRequest={}", JSONObject.toJSONString(request));
            log.trace("ObserveSchedule={}", JSONObject.toJSONString(observeSchedule));
        }
        AIAsianPricerResult aiAsianPricerResult = nativeCpp.AIAsianPricer(request,observeSchedule);
        if (log.isTraceEnabled()) {
            log.trace("AIAsianPricerResult={}", JSONObject.toJSONString(aiAsianPricerResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiAsianPricerResult.getMessage()),aiAsianPricerResult.getMessage());
        return aiAsianPricerResult;
    }

    /**
     * 累计期权
     * @param request 基本参数
     * @param observeSchedule 观察日
     * @param volSurface 波动率
     * @return 计算结果
     */
    public  AIAccumulatorPricerResult AIAccumulatorPricer(AIAccumulatorPricerRequest request, ObserveSchedule[] observeSchedule, VolSurface volSurface){
        if (log.isTraceEnabled()) {
            log.trace("AIAccumulatorPricerRequest={}", JSONObject.toJSONString(request));
            log.trace("ObserveSchedule={}", JSONObject.toJSONString(observeSchedule));
            log.trace("VolSurface={}", JSONObject.toJSONString(volSurface));
        }
        AIAccumulatorPricerResult aiAccumulatorPricerResult = nativeCpp.AIAccumulatorPricer(request,observeSchedule,volSurface);
        if (log.isTraceEnabled()) {
            log.trace("AIAccumulatorPricerResult={}", JSONObject.toJSONString(aiAccumulatorPricerResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiAccumulatorPricerResult.getErrorMessage()),aiAccumulatorPricerResult.getErrorMessage());
        return aiAccumulatorPricerResult;
    }

    /**
     * 雪球计算
     * @param request 雪球参数
     * @param knockoutSchedule 观察日
     * @return 雪球计算结果
     */
    public  AISnowBallPricerResult AISnowBallPricer(AISnowBallPricerRequest request, KnockOutSchedule[] knockoutSchedule){
        if (log.isTraceEnabled()){
            log.trace("AISnowBallPricerRequest:{}",JSONObject.toJSONString(request));
            log.trace("knockoutSchedule:{}",JSONObject.toJSONString(knockoutSchedule));
        }
        AISnowBallPricerResult aiSnowBallPricerResult = nativeCpp.AISnowBallPricer(request,knockoutSchedule);
        if (log.isTraceEnabled()){
            log.trace("aiSnowBallPricerResult:{}",JSONObject.toJSONString(aiSnowBallPricerResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiSnowBallPricerResult.getMessage()),aiSnowBallPricerResult.getMessage());
        return aiSnowBallPricerResult;
    }

    /**
     * 隐含波动率
     * @param request 计算结果
     * @return 隐含波动率
     */
    public  AIBlackImpliedVolResult AIBlackImpliedVol(AIBlackImpliedVolRequest request){
        AIBlackImpliedVolResult aiBlackImpliedVolResult = nativeCpp.AIBlackImpliedVol(request);
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiBlackImpliedVolResult.getErrorMessage()),aiBlackImpliedVolResult.getErrorMessage());
        return aiBlackImpliedVolResult;
    }


    /**
     * 线性插值
     * @param request 计算参数
     * @param volSurface 波动率
     * @return 线性插值返回值
     */
    public  AILinearInterpVolSurfaceResult AILinearInterpVolSurface(AILinearInterpVolSurfaceRequest request,VolSurface volSurface){
        if (log.isTraceEnabled()) {
            log.trace("AILinearInterpVolSurfaceRequest={}", JSONObject.toJSONString(request));
            log.trace("VolSurface={}", JSONObject.toJSONString(volSurface));
        }
        AILinearInterpVolSurfaceResult aiLinearInterpVolSurfaceResult = nativeCpp.AILinearInterpVolSurface(request,volSurface);
        if (log.isTraceEnabled()) {
            log.trace("AILinearInterpVolSurfaceResult={}", JSONObject.toJSONString(aiLinearInterpVolSurfaceResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiLinearInterpVolSurfaceResult.getErrorMessage()),aiLinearInterpVolSurfaceResult.getErrorMessage());
        return aiLinearInterpVolSurfaceResult;
    }

    /**
     * 波动率转换
     * @param volSurface 波动率
     * @return 转换结果
     */
    public  AIDeltaVol2StrikeVolResult AIDeltaVol2StrikeVol(VolSurface volSurface){
        if (log.isTraceEnabled()) {
            log.trace("VolSurface={}", JSONObject.toJSONString(volSurface));
        }
        AIDeltaVol2StrikeVolResult aiDeltaVol2StrikeVolResult = nativeCpp.AIDeltaVol2StrikeVol(volSurface);
        if (log.isTraceEnabled()) {
            log.trace("AIDeltaVol2StrikeVolResult={}", JSONObject.toJSONString(aiDeltaVol2StrikeVolResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiDeltaVol2StrikeVolResult.getMessage()),aiDeltaVol2StrikeVolResult.getMessage());
        return aiDeltaVol2StrikeVolResult;
    }

    /**
     * 欧式期权
     * @param request 计算参数
     * @param observeSchedule 观察日
     * @param volSurface 波动率
     * @return 计算结果
     */
    public AIEnhancedAsianPricerResult AIEnhancedAsianPricer(AIEnhancedAsianPricerRequest request,ObserveSchedule[] observeSchedule, VolSurface volSurface){
        AIEnhancedAsianPricerResult aiEnhancedAsianPricerResult = nativeCpp.AIEnhancedAsianPricer(request,observeSchedule,volSurface);
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiEnhancedAsianPricerResult.getErrorMessage()),aiEnhancedAsianPricerResult.getErrorMessage());
        return aiEnhancedAsianPricerResult;
    }

    /**
     *  熔断累计
     * @param request 计算参数
     * @param observeSchedule 观察日历
     * @param volSurface 波动率
     * @return 计算结果
     */
    public AIKOAccumulatorPricerResult AIKOAccumulatorPricer(AIKOAccumulatorPricerRequest request, ObserveSchedule[] observeSchedule, VolSurface volSurface){
        if (log.isTraceEnabled()) {
            log.trace("AIKOAccumulatorPricer-->request={}", JSONObject.toJSONString(request));
            log.trace("AIKOAccumulatorPricer-->observeSchedule={}", JSONObject.toJSONString(observeSchedule));
            log.trace("AIKOAccumulatorPricer-->VolSurface={}", JSONObject.toJSONString(volSurface));
        }
        AIKOAccumulatorPricerResult aIKOAccumulatorPricerResult = nativeCpp.AIKOAccumulatorPricer(request,observeSchedule,volSurface);
        if (log.isTraceEnabled()) {
            log.trace("AIKOAccumulatorPricer={}", JSONObject.toJSONString(aIKOAccumulatorPricerResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aIKOAccumulatorPricerResult.getErrorMessage()),aIKOAccumulatorPricerResult.getErrorMessage());
        return aIKOAccumulatorPricerResult;
    }

    /**
     *  保险亚式
     * @param request 计算参数
     * @param observeSchedule 观察日历
     * @param volSurface 波动率
     * @return 计算结果
     */
    public AIInsuranceAsianPricerResult AIInsuranceAsianPricer(AIInsuranceAsianPricerRequest request, ObserveSchedule[] observeSchedule, VolSurface volSurface){
        if (log.isTraceEnabled()) {
            log.trace("AIInsuranceAsianPricer-->request={}", JSONObject.toJSONString(request));
            log.trace("AIInsuranceAsianPricer-->observeSchedule={}", JSONObject.toJSONString(observeSchedule));
            log.trace("AIInsuranceAsianPricer-->VolSurface={}", JSONObject.toJSONString(volSurface));
        }
        AIInsuranceAsianPricerResult aiInsuranceAsianPricerResult = nativeCpp.AIInsuranceAsianPricer(request,observeSchedule,volSurface);
        if (log.isTraceEnabled()) {
            log.trace("AIKOAccumulatorPricer={}", JSONObject.toJSONString(aiInsuranceAsianPricerResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiInsuranceAsianPricerResult.getErrorMessage()), aiInsuranceAsianPricerResult.getErrorMessage());
        return aiInsuranceAsianPricerResult;
    }


    /**
     *  折价建仓雪球
     * @param request 计算参数
     * @param observeSchedule 观察日历
     * @param volSurface 波动率
     * @return 计算结果
     */
    public AIDisOpenSnowBallPricerResult AIDisOpenSnowBallPricer(AIDisOpenSnowBallPricerRequest request, KnockOutSchedule[] knockoutSchedule, VolSurface volSurface,DivTermStructure divTermStructure){
        if (log.isTraceEnabled()) {
            log.trace("AIDisOpenSnowBallPricer-->request={}", JSONObject.toJSONString(request));
            log.trace("AIDisOpenSnowBallPricer-->observeSchedule={}", JSONObject.toJSONString(knockoutSchedule));
            log.trace("AIDisOpenSnowBallPricer-->VolSurface={}", JSONObject.toJSONString(volSurface));
        }
        AIDisOpenSnowBallPricerResult aiDisOpenSnowBallPricerResult = nativeCpp.AIDisOpenSnowBallPricer(request,knockoutSchedule,volSurface,divTermStructure);
        if (log.isTraceEnabled()) {
            log.trace("AIDisOpenSnowBallPricer={}", JSONObject.toJSONString(aiDisOpenSnowBallPricerResult));
        }
        BussinessException.E_900001.assertTrue(StringUtils.isBlank(aiDisOpenSnowBallPricerResult.getMessage()), aiDisOpenSnowBallPricerResult.getMessage());
        return aiDisOpenSnowBallPricerResult;
    }
}
