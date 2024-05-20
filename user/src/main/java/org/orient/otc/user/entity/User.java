package org.orient.otc.user.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.user.enums.SexEnums;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author 孔景军
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class User extends BaseEntity implements Serializable
{

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 账号
     */
	private String account;
    /**
     * 密码
     */
    @JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
	private String password;
    /**
     * md5密码盐
     */
    @JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
	private String salt;
    /**
     * 名字
     */
    @ApiModelProperty(value = "姓名")
	private String name;

    /**
     * 镒链账号
     */
    private String ylUser;
    /**
     * 镒链密码
     */
    private String ylPassword;
    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
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
    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色ID")
	private Integer roleId;
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


}
