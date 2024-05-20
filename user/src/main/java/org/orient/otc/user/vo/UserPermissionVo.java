package org.orient.otc.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 潘俊材
 */
@Data
@ApiModel
public class UserPermissionVo {
    /**
     * 权限ID
     */
    private Integer id;
    /**
     * 权限代码
     */
    @ApiModelProperty("权限代码")
    private String permissionCode;
    /**
     * 前端路径
     */
    @ApiModelProperty(value = "前端路径",allowEmptyValue=true)
    private String url;
    /**
     * 权限名称
     */
    @ApiModelProperty("权限名称")
    private String permissionName;
    /**
     * 权限类型
     */
    @ApiModelProperty("权限类型")
    private String permissionType;
    /**
     * 父级ID
     */
    @ApiModelProperty("父级ID")
    private Integer parentId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注",required = false,allowEmptyValue=true)
    private String notes;
    @ApiModelProperty(value = "图标",required = false,allowEmptyValue=true)
    private String icon;

    @ApiModelProperty(value = "是否隐藏（true:隐藏，false:显示）",required = false,allowEmptyValue=true)
    private Boolean hidden;

    @ApiModelProperty(value = "前端组件名称",required = false,allowEmptyValue=true)
    private String name;
    @ApiModelProperty(value = "前端路由地址（访问地址）",required = false,allowEmptyValue=true)
    private String path;
    @ApiModelProperty(value = "前端组件路径",required = false,allowEmptyValue=true)
    private String component;
    @ApiModelProperty(value = "排序",required = false,allowEmptyValue=true)
    private Integer sort;
    @ApiModelProperty(value = "组件是否缓存(true:缓存， false:不缓存)",required = false,allowEmptyValue=true)
    private Boolean isCache;
    @ApiModelProperty(value = "菜单状态(1:正常，0:禁用)",required = false,allowEmptyValue=true)
    private Integer status;
}
