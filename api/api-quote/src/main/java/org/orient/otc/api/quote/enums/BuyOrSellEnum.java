package org.orient.otc.api.quote.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 交易方向
 */
@Getter
public enum BuyOrSellEnum {
    /**
     * 买入
     */
    buy("buy","买入"),
    /**
     * 卖出
     */
    sell("sell","卖出"),
    ;

    private final String key;
    private final String desc;

    BuyOrSellEnum(String key,String desc) {
        this.key=key;
        this.desc = desc;
    }

    public static BuyOrSellEnum getTradeTypeByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (BuyOrSellEnum enums : BuyOrSellEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
    }
    public static BuyOrSellEnum getTradeTypeByDesc(String desc){
        if (StringUtils.isEmpty(desc)){
            return null;
        }
        for (BuyOrSellEnum enums : BuyOrSellEnum.values()){
            if (enums.getDesc().equals(desc)){
                return enums;
            }
        }
        return null;
    }
}
