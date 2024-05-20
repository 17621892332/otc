package org.orient.otc.common.jni.vo;

import lombok.Data;

/**
 * 雪球期权计算结果
 */
@Data
public class AISnowBallPricerResult {
    /**
     * 估值
     */
    double pv;
    double delta;
    double gamma;
    double vegaPercentage;
    double thetaPerDay;
    double rhoPercentage;
    double dividendRhoPercentage;
    String message;
}
