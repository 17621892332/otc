package org.orient.otc.dm.dto.underlyingQuote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class UnderlyingQuoteUpdateDTO {

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
    private List<String> underlyingCodesList;
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
