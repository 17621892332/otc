package org.orient.otc.common.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {
    E_100101(100101,"请重新登录"),
    E_100102(100102,"账号不存在"),
    E_100103(100103,"密码错误"),
    /**
     * 数据异常
     */
    E_100104(100104,"数据异常"),
    E_100105(100105,"token信息异常"),
    E_100106(100106,"权限不足"),

    /**
     * 合约代码不存在
     */
    E_200001(200001,"合约代码不存在"),

    /**
     * 场内账号不存在
     */
    E_200002(200002,"场内账号不存在"),

    /**
     * 获取镒链token异常
     */
    E_900101(900101,"获取镒链token异常"),
    /**
     * 同步至镒链失败
     */
    E_900102(900102,"同步至镒链失败"),
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
