package org.orient.otc.dm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

/**
 * 品种信息(VarietyType)表实体类
 *
 * @author makejava
 * @since 2023-07-14 11:18:49
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class VarietyType extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(value = "产业链ID")
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 品种类型名称
     */
    @ApiModelProperty(value = "产业链名称")
    private String typeName;

    @ApiModelProperty(value = "排序")
    private Integer typeSort;

    }

