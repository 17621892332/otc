package org.orient.otc.yl.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 更新保证金参数
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Data
public class UpdateCustomTradeRiskDTO extends UpdateTradePositionMarginDTO {


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
