package org.orient.otc.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.user.entity.Role;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("簿记账户交易员vo")
public class AssetUnitUserVo {

    @ApiModelProperty(value = "簿记账簿ID")
    private  Integer assetunitId;

    @ApiModelProperty(value = "用户ID")
    private  Integer id;

    /**
     * 头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String account;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;
    /**
     * md5密码盐
     */
    @ApiModelProperty(value = "md5密码盐")
    private String salt;
    /**
     * 名字
     */
    @ApiModelProperty(value = "名字")
    private String name;
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
    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色ID")
    private Integer roleId;
    /**
     * 角色
     */
    @ApiModelProperty(value = "角色信息")
    private List<Role> roles;
    /**
     * 状态(1：启用  2：冻结  3：删除）
     */
    @ApiModelProperty(value = "状态")
    private Integer status;
    /**
     * 保留字段
     */
    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "工号")
    private String jobNumber;
}
