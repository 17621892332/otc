package org.orient.otc.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class SqlConfig extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * code
     */
    @ApiModelProperty( value = "code", required = true)
    private String code;
    /**
     * 描述
     */
    @ApiModelProperty( value = "描述", required = true)
    private String descript;

    /**
     * sql
     */
    @ApiModelProperty( value = "sql", required = true)
    private String sqlText;
    @ApiModelProperty( value = "sql类型(1:查询,2:更新,3:插入,4:更新)", required = true)
    private Integer type;
}
