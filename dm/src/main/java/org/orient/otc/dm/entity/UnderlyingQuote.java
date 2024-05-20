package org.orient.otc.dm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
* 主力标的管理
* @TableName underlying_quote
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class UnderlyingQuote  extends BaseEntity implements Serializable {

    /**
    * ID
    */
    @NotNull(message="[ID]不能为空")
    @ApiModelProperty("ID")
    private Integer id;
    /**
    * 品种Id
    */
    @ApiModelProperty("品种Id")
    private Integer varietyId;
    /**
    * 标的代码
    */
    @ApiModelProperty("标的代码")
    private String underlyingCodes;
    /**
    * 是否需要报价
    */
    @ApiModelProperty("是否需要报价")
    private Boolean needQuote;
    /**
    * 标的交易最小数量
    */
    @ApiModelProperty("标的交易最小数量")
    private Integer minimumAmount;
    /**
    * 行权价间隔
    */
    @ApiModelProperty("行权价间隔")
    private Integer strikeInterval;
    /**
    * 排序
    */
    @ApiModelProperty("排序")
    private Integer sort;
}
