package org.orient.otc.dm.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 品种筛选条件
 */
@Data
public class VarietyByVarietyTypeVO {


    /**
     * 品种代码
     */
    @ApiModelProperty(value = "品种代码")
    private List<String> varietyCodeList;

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
}
