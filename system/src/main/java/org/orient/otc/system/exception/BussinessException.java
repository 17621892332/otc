package org.orient.otc.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseExceptionAssert;

@Getter
@AllArgsConstructor
public enum BussinessException implements BaseExceptionAssert {

    /**
     * 参数缺失
     */
    E_200101(200101,"参数缺失"),
    /**
     * sql执行异常
     */
    E_200102(200102,"sql执行异常"),
    /**
     * 查询数据不能超过20000条
     */
    E_200103(200103,"查询数据不能超过20000条"),
    /**
     * sql的type错误
     */
    E_200104(200104,"sql的type错误"),
    /**
     * 系统参数表为空
     */
    E_200105(200105,"系统参数表为空"),
    /**
     * 存在未平仓的交易记录
     */
    E_200106(200106,"存在未平仓的交易记录"),
    /**
     * 今天不是交易日不能结算
     */
    E_200107(200107,"今天不是交易日不能结算"),
    /**
     * 三点之后才能结算
     */
    E_200108(200108,"三点之后才能结算"),
    /**
     * 结算完后才能切日
     */
    E_200109(200109,"结算完后才能切日"),
    /**
     * 存在未观察的累计期权，请先进行观察，后结算
     */
    E_200110(200110,"存在未观察的累计期权，请先进行观察，后结算"),
    /**
     * 收盘价大于等于敲出价格，请了结相关交易(雪球)，交易编号为
     */
    E_200111(200111,"以下交易编号已满足敲出条件，需要进行敲出"),

    E_200112(200112,"参数错误"),

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
