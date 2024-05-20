package org.orient.otc.system.dto.structure;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 自定义结构新增
 */
@Data
public class StructureAddDTO {

    /**
     * 结构名称
     */
    @ApiModelProperty(value = "结构名称")
    @NotEmpty(message = "结构名称不能为空")
    private String structureName;

    /**
     * 报送结构名称
     */
    @ApiModelProperty(value = "报送结构名称")
    @NotEmpty(message = "报送结构名称不能为空")
    private String submittedStructureName;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortIndex;
}
