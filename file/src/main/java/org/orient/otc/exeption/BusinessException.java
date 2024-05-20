package org.orient.otc.exeption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

/**
 * 异常信息
 * @author dzrh
 */
@Getter
@AllArgsConstructor
public enum BusinessException implements BaseExceptionAssert {
    /**
     * 上传异常
     */
    E_110100(110100, "上传异常"),

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
