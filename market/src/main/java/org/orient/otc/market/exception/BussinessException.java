package org.orient.otc.market.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

/**
 * 行情服务异常
 */
@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {
    /**
     * 参数异常
     */
    E_500001(500001,"参数异常"),
    /**
     * 未取到该合约的行情
     */
    E_500101(500101,"未取到该合约的行情"),
    /**
     * 该日不是交易日
     */
    E_500102(500102,"该日不是交易日"),
    /**
     * 该合约暂无收盘价
     */
    E_500103(500103,"该合约暂无收盘价"),
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
