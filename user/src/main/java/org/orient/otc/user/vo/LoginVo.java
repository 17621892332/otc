package org.orient.otc.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
@ApiModel
public class LoginVo {

    /**
     * token
     */
    @ApiModelProperty(value = "token",required = true)
    private String token;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名",required = true)
    private String name;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Integer id;

    /**
     * 菜单权限
     */
    @ApiModelProperty(value = "菜单权限")
    private List<UserPermissionVo> permission;
}
