package org.orient.otc.api.quote.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class TradeCloseMngVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;
    @ApiModelProperty(value = "平仓序号")
    private Integer sort;
    @ApiModelProperty(value = "平仓数量")
    private BigDecimal closeVolume;
    @ApiModelProperty(value = "平仓日期")
    private LocalDate closeDate;
    @ApiModelProperty(value = "平仓时的行情价格")
    private BigDecimal closeEntryPrice;
    @ApiModelProperty(value = "平仓价格")
    private BigDecimal closePrice;
    @ApiModelProperty(value = "平仓盈亏")
    private BigDecimal profitLoss;

    @ApiModelProperty(value = "mid波动率")
    private BigDecimal midVol;

    @ApiModelProperty(value = "平仓波动率")
    private BigDecimal closeVol;
    /**
     * 平仓分红率
     */
    @ApiModelProperty(value = "平仓分红率")
    private BigDecimal closeDividendYield;

    @ApiModelProperty(value = "平仓金额")
    private BigDecimal closeTotalAmount;

    @ApiModelProperty(value = "平仓名义本金")
    private BigDecimal closeNotionalPrincipal;


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
    @ApiModelProperty(value = "同步状态")
    private  Integer isSync;

    @ApiModelProperty(value = "敲出赔付")
    private BigDecimal knockoutRebate;
    @ApiModelProperty(value = "到期倍数")
    private BigDecimal expireMultiple;
}
