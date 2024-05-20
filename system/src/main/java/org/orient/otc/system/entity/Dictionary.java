package org.orient.otc.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

/**
 * 字典内容
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class Dictionary extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 字典ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String dicId;
    /**
     * 字典类型code
     */
    @ApiModelProperty(value = "字典类型code", required = true)
    private String dicTypeCode;

    /**
     * 字典值
     */
    @ApiModelProperty(value = "字典值", required = true)
    private String dicValue;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    private String dicName;
    /**
     * 字典排序
     */
    @ApiModelProperty(value = "字典排序", required = true)
    private Integer dicSort;
}
