package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 欧式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIAccumulatorPricerRequest {
    /**
     * 期权类型
     */
    private String accumulatorType;

    /**
     * 需要计算的值的序列
     * 默认为“a”，可以是{ "p", "d", "g", "v", "t", "r", "m", "AccuPosition", "AccuPayment"}及其自由组合
     */
    private String valueType;

    /**
     * 东证润和买卖方向
     * 适用于增强亚式期权；如果取值为1，则为东证润和买入，客户卖出；如果取值为-1，则为东证润和卖出，客户买入；累购和累沽默认为1
     */
    private int buySell;

    /**
     * 每日数量
     */
    private double basicQuantity;

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
     * 是否为现金结算
     * {0, 1}
     */
    private int isCashSettled;

    /**
     * 无风险利率
     */
    private double riskFreeInterestRate;

    /**
     * 杠杆系数
     */
    private double leverage;

    /**
     * 单位固定赔付
     */
    private double fixedPayment;

    /**
     * 敲出障碍价格
     */
    private double barrier;

    /**
     * 执行价斜坡
     */
    private double strikeRamp;

    /**
     * 障碍价斜坡
     */
    private double barrierRamp;

    /**
     * 总采价次数，采价天数
     */
    private int totalObservations;

    /**
     * 用于对每个成分期权进行估值的波动率组成的曲面，横轴为虚实程度，纵轴为期权期限
     */
   // private VolSurface volSurface;

    /**
     * 情景价格
     */
    private double scenarioPrice;

}
