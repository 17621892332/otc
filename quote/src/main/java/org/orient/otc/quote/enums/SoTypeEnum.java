package org.orient.otc.quote.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SoTypeEnum {
    /**
     * 香草
     */
    AIVanillaPricer(),

    /**
     * 远期
     */
    AIForwardPricer(),
    /**
     * 亚式
     */
    AIAsianPricer(),
    /**
     * 增强亚式
     */
    AIEnhancedAsianPricer(),
    /**
     * 欧式累计
     */
    AIAccumulatorPricer(),

    /**
     * 雪球
     */
    AISnowBallPricer(),
    /**
     * 隐含波动率
     */
    AIBlackImpliedVol(),
    /**
     * 线性插值
     */
    AILinearInterpVolSurface(),
    /**
     * 波动率曲面转换
     */
    AIDeltaVol2StrikeVol(),

    /**
     * 熔断累计
     */
    AIKOAccumulatorPricer(),
    /**
     * 保险亚式
     */
    AIInsuranceAsianPricer,
    /**
     * 折价建仓雪球
     */
    AIDisOpenSnowBallPricer(),
    /**
     * 单障碍期权
     */
    AISingleBarrierPricer(),
    ;
}
