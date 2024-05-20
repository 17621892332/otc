package org.orient.otc.system.enums;

public enum SuccessStatusEnum {
    success("成功"),
    faild("失败"),
    unexecuted("未执行"),
    ;
    private String desc;

    SuccessStatusEnum(String desc) {
        this.desc = desc;
    }
}
