package org.orient.otc.api.dm.enums;

public enum UnderlyingState {
    Live("有效"),
    Matured("到期");

    private String desc;

    UnderlyingState(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
