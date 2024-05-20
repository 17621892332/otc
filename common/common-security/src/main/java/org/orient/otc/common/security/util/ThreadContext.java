package org.orient.otc.common.security.util;

import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.security.dto.AuthorizeInfo;

/**
 * 全局上下文
 */
@Slf4j
public class ThreadContext {
    private static final ThreadLocal<AuthorizeInfo> AUTHORIZE_INFO = new ThreadLocal<>();

    protected ThreadContext() {
    }

    public static void setAuthorizeInfo(AuthorizeInfo value) {
        if (value == null) {
            deleteAuthorizeInfo();
        } else {
            AUTHORIZE_INFO.set(value);

        }
    }
    public static void deleteAll() {
        AUTHORIZE_INFO.remove();
    }
    public static void deleteAuthorizeInfo() {
        AUTHORIZE_INFO.remove();
    }


    /**
     * 获取用户登录信息
     * @return 用户登录信息
     */
    public static AuthorizeInfo getAuthorizeInfo() {
        return AUTHORIZE_INFO.get();
    }
}
