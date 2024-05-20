package org.orient.otc.quote.vo.quote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.OptionTypeEnum;

import java.math.BigDecimal;

/**
 * 定价计算结果
 * @author dzrh
 */
@Data
public class QuoteStringResultVo {
    /**
     * 顺序
     */
    @ApiModelProperty(value = "顺序", required = true)
    private Integer sort;

    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型", required = true)
    private OptionTypeEnum optionType;

    /**
     * 估值
     */
    @ApiModelProperty("估值")
    private String pv;

    /**
     * 标的价格变化对期权价值的影响
     */
    @ApiModelProperty("标的价格变化对期权价值的影响")
    private String delta;

    /**
     * 标的价格变化对Delta的影响
     */
    @ApiModelProperty("标的价格变化对Delta的影响")
    private  String gamma;

    /**
     * 波动率变化对期权价值的影响
     */
    @ApiModelProperty("波动率变化对期权价值的影响")
    private  String vega;

    /**
     * 时间变化对期权价值的影响
     */
    @ApiModelProperty("时间变化对期权价值的影响")
    private String theta;

    /**
     * 无风险利率变化对期权价值的影响
     */
    @ApiModelProperty("无风险利率变化对期权价值的影响")
    private String rho;

    /**
     * 无风险利率变化对期权价值的影响
     */
    @ApiModelProperty("无风险利率变化对期权价值的影响")
    private String dividendRho;

    @ApiModelProperty(value = "交易编号")
    private String tradeCode;

    /**
     * 期权￥单价
     */
    @ApiModelProperty(value = "期权￥单价")
    private String optionPremium;

    /**
     * 期权%单价
     */
    @ApiModelProperty(value = "期权%单价")
    private String optionPremiumPercent;

    /**
     * Day1 PnL
     */
    @ApiModelProperty(value = "Day1 PnL")
    private String day1PnL;

    /**
     * 保证金￥单价
     */
    @ApiModelProperty(value = "保证金￥单价")
    private String margin;


    /**
     * 保证金占用
     */
    @ApiModelProperty(value = "保证金占用")
    private String useMargin;

    /**
     * 成交金额
     */
    @ApiModelProperty(value = "成交金额")
    private String totalAmount;

    @ApiModelProperty(value = "平仓盈亏")
    private String profitLoss;

}
