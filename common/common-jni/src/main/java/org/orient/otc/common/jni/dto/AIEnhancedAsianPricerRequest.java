package org.orient.otc.common.jni.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 欧式
 * @author dzrh
 */
@Data
@NoArgsConstructor
public class AIEnhancedAsianPricerRequest {
    /**
     * 看涨看跌
     */
    private String optionType;

    /**
     * 标的合约价格
     */
    private double underlyingPrice;

    /**
     * 执行价
     */
    private double strike;

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
     * 需要计算的值的序列
     * 默认为“a”，可以是{ "p", "d", "g", "v", "t", "r", "m", "AccuPosition", "AccuPayment"}及其自由组合
     */
    private String valueType;

    /**
     * 总采价次数，采价天数
     */
    private int totalObservations;

    /**
     * 是否为现金结算
     * {0, 1}
     */
    private int isCashSettled;

    /**
     * 无风险利率
     */
    private double riskFreeInterestRate;

    /**
     * 情景价格
     */
    private double scenarioPrice;

    /**
     * 增强价格
     */
    private double enhancedStrike;
}
