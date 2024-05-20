package org.orient.otc.common.jni.nativeinterface;


import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.jni.dto.*;
import org.orient.otc.common.jni.vo.*;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

/**
 * JNI函数
 */
@Component
@Slf4j
public class NativeCpp {

    static {
        try {


            ApplicationHome home = new ApplicationHome(NativeCpp.class);
            String path = home.getSource().getParentFile().toString();
            System.load(path+"/so/libJni.so");
        }catch (UnsatisfiedLinkError error){
            log.error(error.getMessage(),"加载so失败");
        }
    }

    /**
     * 香草期权
     * @param request 计算参数
     * @return 结算结果
     */
    public native AIVanillaPricerResult AIVanillaPricer(AIVanillaPricerRequest request);

    /**
     * 远期期权
     * @param request 计算参数
     * @return 计算结果
     */
    public native AIForwardPricerResult AIForwardPricer(AIForwardPricerRequest request);
    /**
     *  亚式期权
     * @param request 计算参数
     * @param observeSchedule 观察日列表
     * @return 计算结果
     */
    public native AIAsianPricerResult AIAsianPricer(AIAsianPricerRequest request, ObserveSchedule[] observeSchedule);
    /**
     * 累计期权
     * @param request 基本参数
     * @param observeSchedule 观察日
     * @param volSurface 波动率
     * @return 计算结果
     */
    public native AIAccumulatorPricerResult AIAccumulatorPricer(AIAccumulatorPricerRequest request,ObserveSchedule[] observeSchedule,VolSurface volSurface);
    /**
     * 雪球计算
     * @param request 雪球参数
     * @param knockoutSchedule 观察日
     * @return 雪球计算结果
     */
    public native AISnowBallPricerResult AISnowBallPricer(AISnowBallPricerRequest request, KnockOutSchedule[] knockoutSchedule);
    /**
     * 隐含波动率
     * @param request 计算结果
     * @return 隐含波动率
     */
    public native AIBlackImpliedVolResult AIBlackImpliedVol(AIBlackImpliedVolRequest request);

    /**
     * 线性插值
     * @param request 计算参数
     * @param volSurface 波动率
     * @return 线性插值返回值
     */
    public native AILinearInterpVolSurfaceResult AILinearInterpVolSurface(AILinearInterpVolSurfaceRequest request,VolSurface volSurface);
    /**
     * 波动率转换
     * @param volSurface 波动率
     * @return 转换结果
     */
    public native AIDeltaVol2StrikeVolResult AIDeltaVol2StrikeVol(VolSurface volSurface);
    /**
     * 欧式期权
     * @param request 计算参数
     * @param observeSchedule 观察日
     * @param volSurface 波动率
     * @return 计算结果
     */
    public native AIEnhancedAsianPricerResult AIEnhancedAsianPricer(AIEnhancedAsianPricerRequest request,ObserveSchedule[] observeSchedule, VolSurface volSurface);

    /**
     *  熔断累计
     * @param request 计算参数
     * @param observeSchedule 观察日历
     * @param volSurface 波动率
     * @return 计算结果
     */
    public native AIKOAccumulatorPricerResult AIKOAccumulatorPricer(AIKOAccumulatorPricerRequest request, ObserveSchedule[] observeSchedule, VolSurface volSurface);


    /**
     *  保险亚式
     * @param request 计算参数
     * @param observeSchedule 观察日历
     * @param volSurface 波动率
     * @return 计算结果
     */
    public native AIInsuranceAsianPricerResult AIInsuranceAsianPricer(AIInsuranceAsianPricerRequest request, ObserveSchedule[] observeSchedule, VolSurface volSurface);
    /**
     *  折价建仓雪球
     * @param request 计算参数
     * @param observeSchedule 观察日历
     * @param volSurface 波动率
     * @return 计算结果
     */
    public native AIDisOpenSnowBallPricerResult AIDisOpenSnowBallPricer(AIDisOpenSnowBallPricerRequest request, KnockOutSchedule[] knockoutSchedule, VolSurface volSurface,DivTermStructure divTermStructure);
    /**
     *  单障碍期权
     * @param request 计算参数
     * @param observeSchedule 观察日历
     * @param volSurface 波动率
     * @return 计算结果
     */
    public native AISingleBarrierResult AISingleBarrierPricer(AISingleBarrierRequest request, KnockOutSchedule[] knockoutSchedule, VolSurface volSurface, DivTermStructure divTermStructure);

}
