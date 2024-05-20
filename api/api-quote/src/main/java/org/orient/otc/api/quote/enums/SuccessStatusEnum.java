package org.orient.otc.api.quote.enums;

public enum SuccessStatusEnum {
    success("成功"),
    faild("失败"),
    ;
    private String desc;

    SuccessStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
