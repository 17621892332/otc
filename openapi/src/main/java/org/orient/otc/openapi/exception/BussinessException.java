package org.orient.otc.openapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {
    E_100101(100101,"未取到该合约的行情"),
    E_100102(100102,"该日不是交易日"),
    E_100103(100103,"该日的收盘价未记录"),
    ;

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;
}
