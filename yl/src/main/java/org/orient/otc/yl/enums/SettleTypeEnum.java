package org.orient.otc.yl.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 结算方式
 */
@Getter
public enum SettleTypeEnum {

    /**
     * 现金
     */
    cash(1,"现金"),

    /**
     * 头寸
     */
    physical(0,"头寸"),
    /**
     * 混合
     */
    mix(2,"混合"),
    ;

    private final Integer key;
    private final String desc;
    /**
     * 获取枚举
     * @param key 枚举key
     * @return 枚举
     */
    public static SettleTypeEnum getSettleTypeByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (SettleTypeEnum enums : SettleTypeEnum.values()){
            if (enums.getKey().toString().equals(key)){
                return enums;
            }
        }
        return null;
    }
    SettleTypeEnum(Integer key,String desc) {
        this.key = key;
        this.desc = desc;
    }

}
