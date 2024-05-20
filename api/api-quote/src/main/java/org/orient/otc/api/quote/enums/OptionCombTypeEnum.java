package org.orient.otc.api.quote.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dzrh
 */

@Getter
public enum OptionCombTypeEnum {

    /**
     * 牛市价差
     */
    bullMarketSpread("VE", "牛市价差"),
    /**
     * 熊市价差
     */
    bearMarketSpread("VE", "熊市价差"),
    /**
     * 领式结构
     */
    collarSpread("RE","领式结构"),
    /**
     * 跨式结构
     */
    straddle("STR", "跨式结构"),
    /**
     * 宽跨式结构
     */
    wideStrangle("STA", "宽跨式结构"),
    /**
     * 看涨海鸥
     */
    callTriCollar("CO", "三领口组合"),
    /**
     * 看跌海鸥
     */
    putTriCollar("CO", "三领口组合"),
    /**
     * 蝶式结构
     */
    butterflySpread("VE", "蝶式组合"),
    /**
     * 自定义组合
     */
    customize("O", "自定义组合"),
    ;
    private final String key;
    private final String desc;

    @Override
    public String toString() {
        return this.desc;
    }

    OptionCombTypeEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public static OptionCombTypeEnum getTradeTypeByDesc(String desc){
        if (StringUtils.isEmpty(desc)){
            return null;
        }
        for (OptionCombTypeEnum enums : OptionCombTypeEnum.values()){
            if (enums.getDesc().equals(desc)){
                return enums;
            }
        }
        return null;
    }
    public static OptionCombTypeEnum getTradeTypeByName(String name){
        if (StringUtils.isEmpty(name)){
            return null;
        }
        for (OptionCombTypeEnum enums : OptionCombTypeEnum.values()){
            if (enums.name().equals(name)){
                return enums;
            }
        }
        return null;
    }
}
