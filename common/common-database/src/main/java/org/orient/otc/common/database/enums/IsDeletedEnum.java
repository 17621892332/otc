package org.orient.otc.common.database.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author dzrh
 */
@Getter
public enum IsDeletedEnum {
    NO(0,"正常"),
    YES(1,"已删除");
    @EnumValue
    private final Integer flag;

    private final String desc;
    IsDeletedEnum(Integer flag, String desc){
        this.flag=flag;
        this.desc=desc;
    }
}
