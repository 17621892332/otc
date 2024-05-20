package org.orient.otc.common.jni.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 亚式期权
 * @author dzrh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIInsuranceAsianPricerResult {

    double pv;

    double delta;

    double gamma;

    double vegaPercentage;

    double thetaPerDay;

    double rhoPercentage;

    double dividendRhoPercentage;

    String errorMessage;

    double realizedSettlement;

    double accumulatedPosition;
}
