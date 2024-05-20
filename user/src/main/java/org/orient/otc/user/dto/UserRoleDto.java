package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 用户角色请求对象
 */
@Data
@ApiModel("用户角色请求对象")
public class UserRoleDto {
    /**
     * 角色ID列表
     */
    @ApiModelProperty("角色ID列表")
    private List<Integer> roleIdList;
}
