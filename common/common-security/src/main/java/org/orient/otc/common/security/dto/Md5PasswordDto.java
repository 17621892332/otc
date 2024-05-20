package org.orient.otc.common.security.dto;

import lombok.Data;

@Data
public class Md5PasswordDto {
    /**
     * MD5盐
     */
    private String salt;

    /**
     * MD5加密后的密码
     */
    private String md5Password;
}
