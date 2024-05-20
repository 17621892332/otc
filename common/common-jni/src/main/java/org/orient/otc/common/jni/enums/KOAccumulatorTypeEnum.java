package org.orient.otc.common.jni.enums;

/**
 * 熔断累计期权类型
 */
public enum KOAccumulatorTypeEnum {
    /**
     * 浮动赔付熔断累购期权
     */
    acccall("浮动赔付熔断累购期权"),
    /**
     * 浮动赔付熔断累沽期权
     */
    accput("浮动赔付熔断累沽期权"),
    /**
     * 固定赔付熔断累购期权
     */
    fpcall("固定赔付熔断累购期权"),
    /**
     * 固定赔付熔断累沽期权
     */
    fpput("固定赔付熔断累沽期权");
    private final String desc;

    KOAccumulatorTypeEnum(String desc) {
        this.desc = desc;
    }
}
