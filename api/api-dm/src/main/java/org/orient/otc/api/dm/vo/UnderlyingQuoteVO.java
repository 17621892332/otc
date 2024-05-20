package org.orient.otc.api.dm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * benchmark
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class UnderlyingQuoteVO {
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
     * 产业链
     */
    @ApiModelProperty(value = "产业链")
    private String varietyTypeName;
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
     * 标的代码
     */
    @ApiModelProperty("标的代码")
    private String underlyingCodes;
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
    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private List<String> underlyingManagerList;
}
