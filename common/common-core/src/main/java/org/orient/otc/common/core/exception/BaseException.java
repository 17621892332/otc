package org.orient.otc.common.core.exception;

import lombok.Getter;

/**
 * 基础异常
 */
@Getter
public class BaseException extends RuntimeException {

    private final IExceptionEnum responseEnum;

    /**
     * 基础异常
     * @param responseEnum 异常枚举
     */
    public BaseException(IExceptionEnum responseEnum) {
        super(responseEnum.getMessage());
        this.responseEnum = responseEnum;
    }

    /**
     * 基础异常
     * @param responseEnum 异常枚举
     * @param message 异常信息
     */
    public BaseException(IExceptionEnum responseEnum, String message ) {
        super(message);
        this.responseEnum = responseEnum;
    }

    /**
     * 基础异常
     * @param responseEnum 异常枚举
     * @param t 异常对象
     * @param message 异常信息
     */
    public BaseException(IExceptionEnum responseEnum, Throwable t, String message ) {
        super(message, t);
        this.responseEnum = responseEnum;
    }


}
