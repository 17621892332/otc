package org.orient.otc.common.database.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author dzrh
 */
@Getter
public enum EnabledEnum {
    TRUE(0,"启用"),
    FALSE(1,"禁用");
    @EnumValue
    private final Integer flag;

    private final String desc;
    EnabledEnum(Integer flag, String desc){
        this.flag=flag;
        this.desc=desc;
    }
}
