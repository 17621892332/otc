package org.orient.otc.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公用异常
 */
@Getter
@AllArgsConstructor
public enum CommonExceptionEnum implements BaseExceptionAssert {
    /**
     * 服务器未知错误
     */
    SERVER_ERROR(999999, "服务器未知错误"),
    /**
     * 参数绑定校验异常
     */
    VALID_ERROR(900001, "参数绑定校验异常"),
    /**
     * 稍后重试
     */
    WAIT_RETRY(900002, "稍后重试"),
    /**
     * 服务异常
     */
    SERVICE_ERROR(900003, "服务异常"),
    ;
    /**
     * 返回码
     */
    private final int code;
    /**
     * 返回消息
     */
    private final String message;
}
