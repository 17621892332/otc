package org.orient.otc.message.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

/**
 * @author chengqiang
 */
@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {
    E_100101(100101,"邮箱配置中, 通配符的关键字不能重复, 否则无法识别"),
    E_100102(100102,"邮件模板名称不能重复! "),
    E_100103(100103,"收件人列表不能为空! "),
    E_100104(100104,"邮件模板错误 "),
    E_100105(100105,"结算确认书生成失败, 请联系管理员! "),
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
