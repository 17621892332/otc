package org.orient.otc.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {
    E_100101(100101,"参数缺失"),
    E_100102(100102,"账号不存在"),
    E_100103(100103,"密码错误"),
    E_100104(100104,"账号已注册"),


    E_100201(100201,"角色信息不存在"),
    E_100202(100202,"角色名称不能为空"),
    /**
     * 场内账号已存在
     */
    E_100301(100301,"场内账号已存在"),
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
