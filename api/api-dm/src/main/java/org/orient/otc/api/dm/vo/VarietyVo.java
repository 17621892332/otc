package org.orient.otc.api.dm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class VarietyVo  implements Serializable {
    private static final long serialVersionUID = 1L;


    private Integer id;

    /**
     * 品种代码
     */
    @ApiModelProperty(value = "品种代码")
    private String varietyCode;

    /**
     * 品种名称
     */
    @ApiModelProperty(value = "品种名称")
    private String varietyName;
    /**
     * 产业链ID
     */
    @ApiModelProperty(value  ="产业链ID")
    private Integer varietyTypeId;

    /**
     * 产业链名称
     */
    @ApiModelProperty(value = "产业链名称")
    private String varietyTypeName;
    /**
     * 报价单位
     */
    @ApiModelProperty(value = "报价单位")
    private String quoteUnit;

    /**
     * 最小变动价位
     */
    @ApiModelProperty(value = "最小变动价位")
    private String minPriceChange;


    /**
     * 跌停幅度
     */
    @ApiModelProperty(value = "跌停幅度")
    private BigDecimal upDownLimit;

    /**
     * 跌停幅度
     */
    @ApiModelProperty(value = "跌停幅度")
    private String upDownLimitFormat;
    /**
     * 保证金率
     */
    @ApiModelProperty(value = "保证金率")
    private BigDecimal margin;


    /**
     * 资产类型
     */
    @ApiModelProperty(value = "资产类型")
    private String underlyingAssetType;

    /**
     * 交易员ID
     */
    @ApiModelProperty(value = "交易员ID")
    private Integer traderId;

    /**
     * 推送报价单位
     */
    @ApiModelProperty(value = "推送报价单位")
    private String unit;
}
