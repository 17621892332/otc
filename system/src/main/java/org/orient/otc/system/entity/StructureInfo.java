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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class StructureInfo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 结构名称
     */
    private String structureName;

    /**
     * 报送结构名称
     */
    private String submittedStructureName;

    /**
     * 排序
     */
    private Integer sortIndex;
}
