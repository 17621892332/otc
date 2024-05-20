package org.orient.otc.api.quote.enums;

import lombok.Getter;

/**
 * 结算方式
 */
@Getter
public enum SettleTypeEnum {

    /**
     * 现金
     */
    cash(1,"现金"),

    /**
     * 头寸
     */
    physical(0,"头寸"),
    /**
     * 混合
     */
    mix(2,"混合"),
    ;

    private final Integer key;
    private final String desc;

    SettleTypeEnum(Integer key,String desc) {
        this.key = key;
        this.desc = desc;
    }

}
