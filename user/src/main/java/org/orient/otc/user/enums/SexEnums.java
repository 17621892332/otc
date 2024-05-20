package org.orient.otc.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SexEnums {
    BOY(1),
    GIRL(2),
    ;

    SexEnums(Integer i) {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @EnumValue
    private Integer code;

}
