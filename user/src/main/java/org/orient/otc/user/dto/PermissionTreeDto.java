package org.orient.otc.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("权限树形结构信息")
public class PermissionTreeDto {

    @ApiModelProperty(value = "权限ID")
    private Integer id;

    @ApiModelProperty(value = "权限代码")
    private String permissionCode;

    @ApiModelProperty(value = "权限名称")
    private String permissionName;

    @ApiModelProperty(value = "权限类型")
    private Integer permissionType;

    @ApiModelProperty(value = "父级ID")
    private Integer parentId;

    @ApiModelProperty(value = "备注")
    private String notes;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "是否隐藏")
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

    @ApiModelProperty(value = "子菜单列表")
    List<PermissionTreeDto> childrenList;

}
