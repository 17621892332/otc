package org.orient.otc.common.jni.dto;

import lombok.*;

/**
 * 折价建仓雪球
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIDisOpenSnowBallPricerRequest {
    /**
     * 计算雪球价格使用的算法
     */
    private AlgorithmParameters algorithmParameters;
    /**
     * 看涨看跌
     */
    private String optionType;

    /**
     * 标的资产是否为权益类
     */
    private boolean isEquity;

    /**
     * 定价时点的时间戳
     */
    private long evaluationTime;

    /**
     * 定价时标的合约价格
     */
    private double underlyingPrice;

    /**
     * 无风险利率
     */
    private double riskFreeInterestRate;
    /**
     * 股息率
     */
    private double dividendYield;
    /**
     * 股息率是否是常数
     */
    private boolean isDividendConstant;

    /**
     * 波动率
     */
    private double constantVol;

    /**
     * 返利率
     */
    private RateStruct returnRate;

    /**
     * 红利票息率
     */
    private RateStruct bonusRate;
    /**
     * 敲出观察总次数
     */
    private int totalObservations;
    /**
     * 成交时间点的时间戳
     */
    private long productStartDate;
    /**
     * 到期时间点的时间戳
     */
    private long productEndDate;
    /**
     * 成交时标的合约价格
     */
    private double entryUnderlyingPrice;
    /**
     * 敲入障碍
     */
    private Level knockinBarrier;
    /**
     * 是否敲入
     */
    private boolean isAlreadyKnockedIn;
    /**
     * 敲入后票息是否失效
     */
    private boolean isKnockedInEnd;
    /**
     * 敲入转看跌的执行价格或敲入转熊市价差的高执行价格
     */
    private Level strikeOnceKnockedin;
    /**
     * 敲入后客户建仓的参与率
     */
    private double participationRatio;
    /**
     * 敲入是否头寸结算
     */
    private int isCashSettled;
    /**
     * 敲入后头寸是否已经完成建仓
     */
    private boolean isSpotOpen;
}
