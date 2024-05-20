package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.user.enums.SexEnums;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("保存用户信息")
public class UserSaveDto {
    /**
     * 用户ID
     */
    @NotNull(message = "id不能为空")
    @ApiModelProperty(value = "用户ID",required = true)
    private Integer id;

    /**
     * 账号
     */
    @NotEmpty
    @ApiModelProperty(value = "账号",required = true)
    private String account;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码",required = true)
    private String password;
    /**
     * 名字
     */
    @ApiModelProperty(value = "名字")
    private String name;

    /**
     * 密码
     */
    @NotEmpty(message = "工号不能为空")
    @ApiModelProperty(value = "工号",required = true)
    private String jobNumber;
    /**
     * 生日
     */
    @ApiModelProperty(value = "生日")
    private Date birthday;
    /**
     * 性别（1：男 2：女）
     */
    @ApiModelProperty(value = "性别")
    private Integer sex;
    /**
     * 电子邮件
     */
    @ApiModelProperty(value = "电子邮件")
    private String email;
    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "权限列表")
    private List<Integer> roleIdList;

    @ApiModelProperty(value = "用户状态")
    private Integer status;
}
