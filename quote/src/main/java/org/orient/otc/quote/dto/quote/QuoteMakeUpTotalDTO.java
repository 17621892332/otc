package org.orient.otc.quote.dto.quote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 定价计算组合信息
 */
@Data
public class QuoteMakeUpTotalDTO {

    /**
     * 估值
     */
    @ApiModelProperty("估值")
    private BigDecimal pv;

    /**
     * 标的价格变化对期权价值的影响
     */
    @ApiModelProperty("标的价格变化对期权价值的影响")
    private BigDecimal delta;
    /**
     * 标的价格变化对Delta的影响
     */
    @ApiModelProperty("标的价格变化对Delta的影响")
    private BigDecimal gamma;

    /**
     * 波动率变化对期权价值的影响
     */
    @ApiModelProperty("波动率变化对期权价值的影响")
    private BigDecimal vega;

    /**
     * 时间变化对期权价值的影响
     */
    @ApiModelProperty("时间变化对期权价值的影响")
    private BigDecimal theta;

    /**
     * 无风险利率变化对期权价值的影响
     */
    @ApiModelProperty("无风险利率变化对期权价值的影响")
    private BigDecimal rho;

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
     * 成交数量
     */
    @ApiModelProperty(value = "成交数量")
    private BigDecimal tradeVolume;

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
