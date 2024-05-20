package org.orient.otc.common.jni.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单障碍期权
 */
@Data
@NoArgsConstructor
public class AISingleBarrierRequest {
    /**
     * 看涨看跌
     */
    private String optionType;

    /**
     * 标的资产是否为权益类
     */
    private boolean isEquity;

    /**
     * 定价时标的合约价格
     */
    private double underlyingPrice;
    /**
     * 定价时点的时间戳
     */
    private long evaluationTime;
    /**
     * 到期时间点的时间戳
     */
    private long expiryTime;
    /**
     * 敲出观察序列
     */
    private KnockOutSchedule knockOutSchedule;
    /**
     * 敲出观察总次数
     */
    private int totalObservations;
    /**
     * 敲入障碍
     */
    private Level strike1;

    /**
     * 敲入障碍
     */
    private Level strike2;
    /**
     * 敲入后客户建仓的参与率
     */
    private double participationRatio;

    /**
     * 返利率
     */
    private RateStruct returnRate;

    /**
     * 红利票息率
     */
    private RateStruct bonusRate;

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
     * 卷积网格数
     */
    private int gridNumber;

}
