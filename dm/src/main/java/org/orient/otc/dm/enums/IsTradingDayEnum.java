package org.orient.otc.dm.enums;

public enum IsTradingDayEnum {
    tradingday("交易日"),
    nontradingday("非交易日");

    private String desc;

    IsTradingDayEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
