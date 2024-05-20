package org.orient.otc.yl.enums;

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
    bullMarketSpread("bullMarketSpread", "牛市价差"),
    /**
     * 熊市价差
     */
    bearMarketSpread("bearMarketSpread", "熊市价差"),
    /**
     * 领式结构
     */
    collarSpread("collarSpread","风险逆转"),
    /**
     * 跨式结构
     */
    straddle("straddle", "跨式组合"),
    /**
     * 宽跨式结构
     */
    wideStrangle("wideStrangle", "宽跨式组合"),
    /**
     * 看涨海鸥
     */
    callTriCollar("callTriCollar", "三领口组合"),
    /**
     * 看跌海鸥
     */
    putTriCollar("putTriCollar", "三领口组合"),
    /**
     * 蝶式结构
     */
    butterflySpread("butterflySpread", "蝶式组合"),
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

    public static OptionCombTypeEnum getTradeTypeByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (OptionCombTypeEnum enums : OptionCombTypeEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
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
}
