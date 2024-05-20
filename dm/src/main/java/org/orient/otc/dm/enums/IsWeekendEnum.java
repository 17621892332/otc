package org.orient.otc.dm.enums;

import java.util.Set;

public enum IsWeekendEnum {
    weekend("周末"),
    noweekend("非周末");

    private String desc;

    IsWeekendEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
