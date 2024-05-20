package org.orient.otc.api.quote.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 看涨看跌
 */
@Getter
public enum CallOrPutEnum {
    /**
     * 看涨
     */
    call("call","看涨"),
    /**
     * 看跌
     */
    put("put","看跌")
    ;
    private final String key;
    private final String desc;

    CallOrPutEnum(String key,String desc) {
        this.key=key;
        this.desc = desc;
    }

    public static CallOrPutEnum getTradeTypeByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (CallOrPutEnum enums : CallOrPutEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
    }
    public static CallOrPutEnum getTradeTypeByDesc(String desc){
        if (StringUtils.isEmpty(desc)){
            return null;
        }
        for (CallOrPutEnum enums : CallOrPutEnum.values()){
            if (enums.getDesc().equals(desc)){
                return enums;
            }
        }
        return null;
    }

}
