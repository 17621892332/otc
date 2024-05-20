package org.orient.otc.client.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

/**
 * 客户服务异常
 */
@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {
    /**
     * 参数缺失
     */
    E_700101(700101,"参数缺失"),
    /**
     * 参数错误
     */
    E_700102(700102,"参数错误"),
    /**
     * 配置错误
     */
    E_700103(700103,"配置错误"),
    /**
     * 名称不能重复
     */
    E_700104(700104,"名称不能重复"),
    /**
     * 客户不存在
     */
    E_700105(700105,"客户不存在"),
    /**
     * 客户等级不能删除
     */
    E_700106(700106,"客户等级不能删除"),
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
