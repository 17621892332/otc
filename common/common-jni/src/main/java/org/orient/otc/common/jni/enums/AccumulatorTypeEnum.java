package org.orient.otc.common.jni.enums;

/**
 * 期权类型
 */
public enum AccumulatorTypeEnum {
    /**
     * 增强亚式看涨期权
     */
    ascall(),
    /**
     * 增强亚式看跌期权
     */
    asput(),
    /**
     * 浮动赔付累购期权
     */
    acccall(),
    /**
     * 浮动赔付累沽期权
     */
    accput(),
    /**
     * 固定赔付累购期权
     */
    fpcall(),
    /**
     * 固定赔付累沽期权
     */
    fpput(),
    /**
     * 熔断增强累购
     */
    acccallplus,
    /**
     * 熔断增强累沽
     */
    accputplus,

    ;



    AccumulatorTypeEnum() {
    }
}
