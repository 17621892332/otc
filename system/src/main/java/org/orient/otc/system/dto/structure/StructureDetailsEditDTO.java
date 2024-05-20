package org.orient.otc.system.dto.structure;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 自定义结构新增
 */
@Data
public class StructureDetailsEditDTO{

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 结构ID
     */
    @ApiModelProperty(value = "结构ID")
    @NotNull(message = "结构ID不能为空")
    private Integer structureId;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    @NotEmpty(message = "字段名称不能为空")
    private String columnName;

    /**
     * 字段类型:0:文本;1:数值;2:日期;3:单选列表;4:多选列表;
     */
    @ApiModelProperty(value = "字段类型:0:文本;1:数值;2:日期;3:单选列表;4:多选列表;")
    @NotNull(message = "字段类型不能为空")
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
    @NotNull(message = "字段报送名称不能为空")
    private String submittedFieldName;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortIndex;
}
