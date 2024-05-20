package org.orient.otc.quote.vo.quote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.OptionTypeEnum;

import java.math.BigDecimal;

@Data
public class QuoteResultVo {
    /**
     * 顺序
     */
    @ApiModelProperty(value = "顺序", required = true)
    private Integer sort;

    @ApiModelProperty(value = "交易编号")
    private String tradeCode;


    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型", required = true)
    private OptionTypeEnum optionType;

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
    private  BigDecimal gamma;

    /**
     * 波动率变化对期权价值的影响
     */
    @ApiModelProperty("波动率变化对期权价值的影响")
    private  BigDecimal vega;

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
     * 股息率变化对期权价值的影响
     */
    @ApiModelProperty("股息率变化对期权价值的影响")
    private BigDecimal dividendRho;

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
     * 保证金占用
     */
    @ApiModelProperty(value = "保证金占用")
    private BigDecimal useMargin;

    /**
     * 成交金额
     */
    @ApiModelProperty(value = "成交金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "平仓盈亏")
    private BigDecimal profitLoss;

}
