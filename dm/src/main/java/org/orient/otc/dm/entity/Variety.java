package org.orient.otc.dm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class Variety extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
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
     * 报价单位
     */
    @ApiModelProperty(value = "报价单位")
    private String quoteUnit;

    /**
     * 推送报价单位
     */
    @ApiModelProperty(value = "推送报价单位")
    private String unit;

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
     * 保证金率
     */
    @ApiModelProperty(value = "保证金率")
    private BigDecimal margin;
}
