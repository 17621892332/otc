package org.orient.otc.common.jni.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 折价建仓雪球返回值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIDisOpenSnowBallPricerResult {
    double pv;
    double delta;
    double gamma;
    double vegaPercentage;
    double thetaPerDay;
    double rhoPercentage;
    double dividendRhoPercentage;
    String message;
}
