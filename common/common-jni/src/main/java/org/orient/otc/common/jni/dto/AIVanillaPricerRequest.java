package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 香草
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AIVanillaPricerRequest {
    /**
     * 看涨看跌(传CALL, PUT)
     */
    @NonNull
    String optionType;

    /**
     * 标的合约价格
     */
    double underlyingPrice;

    /**
     * 执行价
     */
    double strike;

    /**
     * 定价时点的时间戳
     */
    long evaluationTime;

    /**
     * 到期时间点的时间戳
     */
    long expiryTime;

    /**
     * 无风险利率
     */
    double riskFreeInterestRate;

    /**
     * 股息率
     */
    double dividendYield;

    /**
     * 波动率
     */
    double volatility;
}
