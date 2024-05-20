package org.orient.otc.api.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义结构新增
 */
@Data
public class StructureInfoVO {

    private Integer id;

    /**
     * 结构名称
     */
    @ApiModelProperty(value = "结构名称")
    private String structureName;

    /**
     * 报送结构名称
     */
    @ApiModelProperty(value = "报送结构名称")
    private String submittedStructureName;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortIndex;
}
