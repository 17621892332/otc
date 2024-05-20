package org.orient.otc.quote.enums;

public enum OpenOrCloseEnum {
    open("开仓"),
    close("平仓"),
    ;
    private String desc;

    OpenOrCloseEnum(String desc) {
        this.desc = desc;
    }
}
