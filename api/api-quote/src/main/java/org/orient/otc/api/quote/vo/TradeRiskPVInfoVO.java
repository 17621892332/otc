package org.orient.otc.api.quote.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 日终持仓保证金
 * @author dzrh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeRiskPVInfoVO {

    /**
     * 交易编号
     */
    private String tradeCode;

    /**
     * 交易日期
     */
    private LocalDate riskDate;

    /**
     * 持仓保证金
     */
    private BigDecimal margin;


    /**
     * 估值
     */
    private BigDecimal availableAmount;

    /**
     * 标的价格变化对期权价值的影响
     */
    private BigDecimal delta;
    /**
     * 标的价格变化对Delta的影响
     */
    private BigDecimal gamma;

    /**
     * 波动率变化对期权价值的影响
     */
    private BigDecimal vega;

    /**
     * 时间变化对期权价值的影响
     */
    private BigDecimal theta;

    /**
     * 无风险利率变化对期权价值的影响
     */
    private BigDecimal rho;
}
