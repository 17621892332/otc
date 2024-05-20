package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

/**
 * 角色保存对象
 */
@Data
@ApiModel("角色分页查询对象")
public class RolePageListDto extends BasePage {

    @ApiModelProperty("角色ID")
    private Integer id;

    @ApiModelProperty("角色父ID")
    private Integer pid;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty(value = "备注")
    private String notes;

}
