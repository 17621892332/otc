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
public class TradeCloseMngFeignVo  implements Serializable {
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
    @ApiModelProperty(value = "平仓波动率")
    private BigDecimal closeVol;
    @ApiModelProperty(value = "平仓金额")
    private BigDecimal closeTotalAmount;
    @ApiModelProperty(value = "平仓名义本金")
    private BigDecimal closeNotionalPrincipal;
}
