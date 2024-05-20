package org.orient.otc.netty.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

/**
 * 通讯服务异常
 */
@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {
    /**
     * 调用python异常
     */
    E_400101(400101,"调用python异常"),
    /**
     * 交易数据未匹配，请确认数据是否正常
     */
    E_400201(400201,"交易数据未匹配，请确认数据是否正常"),
    /**
     * 暂不支持该期权的情景分析
     */
    E_400202(400202,"暂不支持该期权的情景分析"),
    /**
     * 价格下限必须小于价格上限
     */
    E_400203(400203,"价格下限必须小于或等于价格上限"),
    /**
     * 波动率下限必须小于或等于波动率上限
     */
    E_400204(400204,"波动率下限必须小于或等于波动率上限"),
    /**
     * 日期下限必须小于或等于日期上限
     */
    E_400205(400205,"日期下限必须小于或等于日期上限"),
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
