package org.orient.otc.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class StructureDetails extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 结构ID
     */
    private Integer structureId;

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段类型:0:文本;1:数值;2:日期;3:单选列表;4:多选列表;
     */
    private Integer columnType;

    /**
     * 下拉列表内容逗号间隔
     */
    private String columnOptions;

    /**
     * 字段默认值
     */
    private String columnDefaultValue;

    /**
     * 字段报送名称
     */
    private String submittedFieldName;

    /**
     * 排序
     */
    private Integer sortIndex;
}
