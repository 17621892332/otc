package org.orient.otc.common.jni.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 香草so返回值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIVanillaPricerResult {
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
