package org.orient.otc.api.dm.enums;

public enum MainContractEnum {
    yes("是主力合约"),
    no("不是主力合约");

    private String desc;

    MainContractEnum(String desc) {
        this.desc = desc;
    }
}
