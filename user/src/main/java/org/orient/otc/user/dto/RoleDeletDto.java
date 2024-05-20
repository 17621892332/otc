package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 角色删除对象
 */
@Data
@ApiModel("角色删除对象")
public class RoleDeletDto {
    @ApiModelProperty("角色ID")
    @NotNull(message="id不能为空")
    private Integer id;
}
