package org.orient.otc.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "角色")
public class RoleVo {
    @ApiModelProperty(value = "角色ID")
    private Integer id;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "备注")
    private String notes;

    @ApiModelProperty("权限ID列表")
    List<Integer> permissionIds;
}
