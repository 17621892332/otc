package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@ApiModel("权限新增信息")
public class PermissionAddDto {
    @ApiModelProperty(value = "权限ID")
    private Integer id;

    @ApiModelProperty(value = "权限代码")
    @NotEmpty(message = "权限代码不能为空")
    private String permissionCode;

    @ApiModelProperty(value = "权限名称")
    @NotEmpty(message = "权限名称不能为空")
    private String permissionName;

    @ApiModelProperty(value = "权限类型")
    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;

    @ApiModelProperty(value = "父级ID")
    @NotNull(message = "父级ID不能为空")
    private Integer parentId;
    /**
     * 权限所属类别
     */
    @ApiModelProperty(value = "限所属类别")
    @NotNull(message = "权限所属类别")
    private Integer type;

    @ApiModelProperty(value = "备注")
    private String notes;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "是否隐藏")
    @NotNull(message = "是否隐藏不能为空")
    private Boolean hidden;

    @ApiModelProperty(value = "前端组件名称")
    private String name;

    @ApiModelProperty(value = "前端路由地址")
    private String path;

    @ApiModelProperty(value = "前端组件路径")
    private String component;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "组件是否缓存")
    private Boolean isCache;

    @ApiModelProperty(value = "菜单状态")
    private Integer status;

}
