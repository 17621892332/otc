package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 雪球
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AISnowBallPricerRequest {
    /**
     * 计算雪球价格使用的算法
     */
    AlgorithmParameters algorithmParameters;
    /**
     * 看涨看跌 {CALL, PUT}
     */
    private String optionType;

    /**
     * 定价时点的时间戳
     */
    long evaluationTime;

    /**
     * 定价时标的合约价格
     */
    double underlyingPrice;

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

    /**
     * 返利率
     */
    RateStruct returnRate;

    /**
     * 红利票息率
     */
    RateStruct bonusRate;

    /**
     * 敲出观察总次数
     */
    int totalObservations;

    /**
     * 成交时间点的时间戳
     */
    long productStartDate;

    /**
     * 到期时间点的时间戳
     */
    long productEndDate;

    /**
     * 成交时标的合约价格
     */
    double entryUnderlyingPrice;

    /**
     * 敲入障碍
     */
    Level knockinBarrier;

    /**
     * 是否敲入
     */
    boolean alreadyKnockedIn;

    /**
     * 敲入转看跌的执行价格或敲入转熊市价差的高执行价格
     */
    Level strikeOnceKnockedin;

    /**
     * 敲入转看跌则为0；敲入转熊市价差的低执行价格
     */
    Level strike2OnceKnockedin;

}
