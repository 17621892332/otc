package org.orient.otc.quote.vo.quote;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 通用定价计算结果
 */
@Data
public class QuoteSoResultVO {
    /**
     * 估值
     */
    private BigDecimal pv;

    /**
     * 标的价格变化对期权价值的影响
     */
    private BigDecimal delta;

    /**
     * 标的价格变化对Delta的影响
     */
    private  BigDecimal gamma;

    /**
     * 波动率变化对期权价值的影响
     */
    private  BigDecimal vega;

    /**
     * 时间变化对期权价值的影响
     */
    private BigDecimal theta;

    /**
     * 无风险利率变化对期权价值的影响
     */
    private BigDecimal rho;

    /**
     * 股息率变化对期权价值的影响
     */
    private BigDecimal dividendRho;


    private BigDecimal accumulatedPosition;

    private BigDecimal accumulatedPayment;

    private BigDecimal accumulatedPnl;

}
