package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 获取角色对象
 */
@Data
@ApiModel("获取角色对象")
public class RoleDetailDto {
    @ApiModelProperty("角色ID")
    @NotNull(message="id不能为空")
    private Integer id;
}
