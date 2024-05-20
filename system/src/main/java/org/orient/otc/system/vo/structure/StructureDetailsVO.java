package org.orient.otc.system.vo.structure;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义结构详情
 */
@Data
public class StructureDetailsVO {

    private Integer id;
    /**
     * 结构ID
     */
    @ApiModelProperty(value = "结构ID")
    private Integer structureId;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String columnName;

    /**
     * 字段类型:0:文本;1:数值;2:日期;3:单选列表;4:多选列表;
     */
    @ApiModelProperty(value = "字段类型:0:文本;1:数值;2:日期;3:单选列表;4:多选列表;")
    private Integer columnType;

    /**
     * 下拉列表内容逗号间隔
     */
    @ApiModelProperty(value = "下拉列表内容逗号间隔")
    private String columnOptions;

    /**
     * 字段默认值
     */
    @ApiModelProperty(value = "字段默认值")
    private String columnDefaultValue;

    /**
     * 字段报送名称
     */
    @ApiModelProperty(value = "字段报送名称")
    private String submittedFieldName;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortIndex;
}
