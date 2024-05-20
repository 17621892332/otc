package org.orient.otc.api.netty.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dzrh
 */
@Data
public class RiskInfoVo {

    @ApiModelProperty(value = "品种代码")
    private String varietyCode;

    /**
     * 标的合约
     */
    @ApiModelProperty(value = "标的合约")
    private String underlyingCode;

    /**
     * 合约实时行情
     */
    private BigDecimal lastPrice;


    /**
     * 手动输入的合约价格
     */
    private BigDecimal editPrice;
    /**
     * 涨跌(%)
     */
    private BigDecimal chg;
    /**
     * 涨跌(%)
     */
    private String chgPercent;

    private BigDecimal deltaLots;

    private BigDecimal deltaAdjustment;

    private BigDecimal deltaCash;

    private BigDecimal gammaLots;

    private BigDecimal gammaCash;

    private BigDecimal theta;

    private BigDecimal vega;

    /**
     * 平衡变动
     */
    private BigDecimal balancedChanges;

    private BigDecimal todayPnL;

    private BigDecimal day1PnL;

    private BigDecimal totalPnl;

    private BigDecimal totalPnlByClose;

    private BigDecimal deltaRl;

}
