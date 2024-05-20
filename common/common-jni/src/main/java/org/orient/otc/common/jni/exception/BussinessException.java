package org.orient.otc.common.jni.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

/**
 * SO异常
 */
@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {
    /**
     * SO异常
     */
    E_900001(900001,"调用so异常"),
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
