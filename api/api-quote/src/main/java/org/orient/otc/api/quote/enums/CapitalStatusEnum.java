package org.orient.otc.api.quote.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/** 资金状态
 * @author dzrh
 */

@Getter
public enum CapitalStatusEnum {

    unconfirmed("unconfirmed", "未确认"),
    settlement("settlement", "已结算"),
    confirmed("confirmed", "已确认"),
    refuse("refuse", "拒绝"),
    updateedUnconfirmed("updateedUnconfirmed", "修改待确认")
    ;
    private final String key;
    private final String desc;

    @Override
    public String toString() {
        return this.desc;
    }

    CapitalStatusEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public static CapitalStatusEnum getCapitalStatusByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (CapitalStatusEnum enums : CapitalStatusEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
    }
    public static CapitalStatusEnum getCapitalStatusByDesc(String desc){
        if (StringUtils.isEmpty(desc)){
            return null;
        }
        for (CapitalStatusEnum enums : CapitalStatusEnum.values()){
            if (enums.getDesc().equals(desc)){
                return enums;
            }
        }
        return null;
    }
}
