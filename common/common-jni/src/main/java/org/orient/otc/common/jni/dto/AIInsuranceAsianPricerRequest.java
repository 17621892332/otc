package org.orient.otc.common.jni.dto;

import lombok.*;

/**
 * 欧式
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIInsuranceAsianPricerRequest {
    /**
     * 看涨看跌
     */
    private String callPut;
    /**
     * 保底封顶
     */
    private String ceilFloor;

    /**
     * 标的合约价格
     */
    private double underlyingPrice;

    /**
     * 执行价1
     */
    private double strike1;

    /**
     * 执行价2
     */
    private double strike2;

    /**
     * 折扣率
     */
    private double discountRate;
    /**
     * 定价时点的时间戳
     */
    private long evaluationTime;
    /**
     * 到期时间点的时间戳
     */
    private long expiryTime;
    /**
     * 常数波动率
     */
    private double constantVol;

    /**
     * 总采价次数，采价天数
     */
    private int totalObservations;

    /**
     * 无风险利率
     */
    private double riskFreeInterestRate;

    /**
     * 分红率
     */
    private double dividendRate;

    /**
     * 模拟路径数
     */
    private int pathNumber;
    /**
     * 线程数
     */
    private int threadNumber;
}
