package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 登录接口请求参数
 */
@Data
@ApiModel
public class UserLoginDTO {
    @NotEmpty(message = "账号不能为空")
    @ApiModelProperty(value = "账号",required = true)
    private String account;
    @NotEmpty(message = "密码不能为空")
    @ApiModelProperty(value = "密码",required = true)
    private String password;

    /**
     * 登录来源: 0客户端 1后台系统
     */
    @NotNull(message = "登录来源不能为空")
    private Integer loginFrom;
}
