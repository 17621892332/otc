package org.orient.otc.quote.vo.quote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteMakeUpTotalVo {

    /**
     * 估值
     */
    @ApiModelProperty("估值")
    BigDecimal pv;

    /**
     * 标的价格变化对期权价值的影响
     */
    @ApiModelProperty("标的价格变化对期权价值的影响")
    BigDecimal delta;

    /**
     * 标的价格变化对Delta的影响
     */
    @ApiModelProperty("标的价格变化对Delta的影响")
    BigDecimal gamma;

    /**
     * 波动率变化对期权价值的影响
     */
    @ApiModelProperty("波动率变化对期权价值的影响")
    BigDecimal vega;

    /**
     * 时间变化对期权价值的影响
     */
    @ApiModelProperty("时间变化对期权价值的影响")
    BigDecimal theta;

    /**
     * 无风险利率变化对期权价值的影响
     */
    @ApiModelProperty("无风险利率变化对期权价值的影响")
    BigDecimal rho;

    /**
     * 期权￥单价
     */
    @ApiModelProperty(value = "期权￥单价")
    private BigDecimal optionPremium;

    /**
     * 期权%单价
     */
    @ApiModelProperty(value = "期权%单价")
    private BigDecimal optionPremiumPercent;

    /**
     * Day1 PnL
     */
    @ApiModelProperty(value = "Day1 PnL")
    private BigDecimal day1PnL;

    /**
     * 保证金￥单价
     */
    @ApiModelProperty(value = "保证金￥单价")
    private BigDecimal margin;

    /**
     * 成交金额
     */
    @ApiModelProperty(value = "成交金额")
    private BigDecimal totalAmount;

    /**
     * 平仓盈亏
     */
    @ApiModelProperty(value = "平仓盈亏")
    private BigDecimal profitLoss;
}
