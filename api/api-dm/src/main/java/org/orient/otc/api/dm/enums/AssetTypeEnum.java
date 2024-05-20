package org.orient.otc.api.dm.enums;

import lombok.Getter;

/**
 * 资产类型
 */
@Getter
public enum AssetTypeEnum {
    /**
     * 商品
     */
    CO("商品"),
    /**
     * 利率
     */
    IR("利率"),
    /**
     * 权益
     */
    EQ("权益"),
    /**
     * 外汇
     */
    FX("外汇"),
    /**
     * 信用
     */
    CR("信用");

    private final String desc;

    AssetTypeEnum(String desc) {
        this.desc = desc;
    }


}
