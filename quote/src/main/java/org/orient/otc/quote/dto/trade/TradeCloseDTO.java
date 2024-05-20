package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 平仓参数
 */
@Data
public class TradeCloseDTO {
    @ApiModelProperty(value = "交易编号")
    @NotNull(message = "交易编号不能为空")
    private String tradeCode;

    @ApiModelProperty(value = "平仓数量")
    private BigDecimal closeVolume;

    @ApiModelProperty(value = "平仓日期")
    @NotNull(message = "平仓日期不能为空")
    private LocalDate closeDate;

    @ApiModelProperty(value = "平仓时的行情价格")
    @NotNull(message = "平仓时的行情价格不能为空")
    private BigDecimal closeEntryPrice;

    @ApiModelProperty(value = "平仓价格")
    @NotNull(message = "平仓价格不能为空")
    private BigDecimal closePrice;

    @ApiModelProperty(value = "平仓盈亏")
    @NotNull(message = "平仓盈亏不能为空")
    private BigDecimal profitLoss;

    @ApiModelProperty(value = "平仓波动率")
    private BigDecimal closeVol;


    @ApiModelProperty(value = "mid波动率")
    private BigDecimal midVol;
    /**
     * 平仓分红率
     */
    @ApiModelProperty(value = "平仓分红率")
    private BigDecimal closeDividendYield;

    @ApiModelProperty(value = "平仓名义本金")
    private BigDecimal closeNotionalPrincipal;

    @ApiModelProperty(value = "平仓金额")
    @NotNull(message = "平仓金额不能为空")
    private BigDecimal closeTotalAmount;


    @ApiModelProperty(value = "Day1 PnL")
    private BigDecimal day1PnL;

    @ApiModelProperty(value = "TTM")
    private BigDecimal ttm;

    @ApiModelProperty(value = "工作日")
    private Integer workday;

    @ApiModelProperty(value = "交易日")
    private Integer tradingDay;

    @ApiModelProperty(value = "公共假日")
    private Integer bankHoliday;


    @ApiModelProperty(value = "pv")
    private BigDecimal pv;

    @ApiModelProperty(value = "delta")
    private BigDecimal delta;

    @ApiModelProperty(value = "gamma")
    private BigDecimal gamma;

    @ApiModelProperty(value = "vega")
    private BigDecimal vega;

    @ApiModelProperty(value = "theta")
    private BigDecimal theta;

    @ApiModelProperty(value = "rho")
    private BigDecimal rho;

    @ApiModelProperty(value = "敲出赔付")
    private BigDecimal knockoutRebate;
    @ApiModelProperty(value = "到期倍数")
    private BigDecimal expireMultiple;
}
