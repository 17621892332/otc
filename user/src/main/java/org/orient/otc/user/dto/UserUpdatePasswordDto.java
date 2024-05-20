package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("跟新用户密码信息")
public class UserUpdatePasswordDto {
    @NotNull
    @ApiModelProperty(value = "用户ID",required = true)
    private Integer id;

    /*@ApiModelProperty(value = "账号",required = true)
    private String account;*/

    @ApiModelProperty(value = "密码",required = true)
    @NotNull
    private String password;

}
