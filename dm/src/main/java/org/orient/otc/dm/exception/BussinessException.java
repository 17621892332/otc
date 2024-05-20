package org.orient.otc.dm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

/**
 * 数据管理服务异常
 */
@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {

    /**
     * 参数缺失
     */
    E_600101(600101, "参数缺失"),
    /**
     * 获取本机ip异常
     */
    E_600102(600102, "获取本机ip异常"),

    /**
     * 参数异常
     */
    E_600103(600103, "参数异常"),
    /**
     * 时间日历没配置
     */

    E_600104(600104, "时间日历没配置"),
    /**
     * 合约不存在
     */

    E_620001(620001, "合约不存在"),
    /**
     * 请先设置主力合约的波动率
     */

    E_620002(620002, "请先设置主力合约的波动率"),
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
