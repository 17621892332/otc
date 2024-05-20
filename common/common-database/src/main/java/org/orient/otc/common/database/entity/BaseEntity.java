package org.orient.otc.common.database.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BaseEntity {
    /**
     * 是否删除
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private int isDeleted;
    /**
     * 创建用户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private Integer creatorId;

    /**
     * 最后更新用户ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private Integer updatorId;

    /**
     * 记录创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private LocalDateTime updateTime;
}
