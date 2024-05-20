package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 角色保存对象
 */
@Data
@ApiModel("角色保存对象")
public class RoleSaveDto {

    /**
     * 角色ID
     */
    @ApiModelProperty("角色ID： 新增时不传")
    private Integer id;

    /**
     * 角色名称
     */
    @ApiModelProperty("角色名称")
    @NotEmpty
    private String roleName;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String notes;

    @ApiModelProperty("权限ID列表")
    List<Integer> permissionIds;

}
