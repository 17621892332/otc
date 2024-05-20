package org.orient.otc.common.jni.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 熔断累计返回值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIKOAccumulatorPricerResult {
    double pv;
    double delta;
    double gamma;
    double vegaPercentage;
    double thetaPerDay;
    double rhoPercentage;
    double dividendRhoPercentage;
    String errorMessage;

    double accumulatedPosition;

    double accumulatedPayment;

    double accumulatedPnl;
}
