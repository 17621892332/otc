package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * 亚式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIAsianPricerRequest {
    /**
     * 看涨看跌(传CALL, PUT)
     */
    @NonNull
    String optionType;

    /**
     * 标的合约价格
     */
    @NonNull
    double underlyingPrice;

    /**
     * 定价时点的时间戳
     */
    @NonNull
    long evaluationTime;

    /**
     * 执行价
     */
    @NonNull
    double strike;

    /**
     * 到期时间点的时间戳
     */
    @NonNull
    long expiryTime;

    /**
     * 无风险利率
     */
    @NonNull
    double riskFreeInterestRate;

    /**
     * 波动率
     */
    @NonNull
    double volatility;

    /**
     * 总采价次数
     */
    @NonNull
    int totalObservations;


}
