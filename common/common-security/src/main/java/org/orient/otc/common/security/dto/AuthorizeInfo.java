package org.orient.otc.common.security.dto;

import lombok.Data;

import java.util.List;

/**
 * @author dzrh
 */
@Data
public class AuthorizeInfo {
    /**
     * 主键id
     */
    private Integer id;
    /**
     * 账号
     */
    private String account;

    /**
     * 名字
     */
    private String name;

    /**
     * 密码
     */
    private String password;
    /**
     * md5密码盐
     */
    private String salt;


    /**
     * 状态(1：启用  2：冻结  3：删除）
     */
    private Integer status;

    /**
     * 登录来源: 0客户端 1后台系统
     */
    private Integer loginForm;

    /**
     * 权限列表
     */
    private List<String> permissionList;
}
