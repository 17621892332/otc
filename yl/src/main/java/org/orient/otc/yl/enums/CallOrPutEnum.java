package org.orient.otc.yl.enums;

import org.apache.commons.lang3.StringUtils;

public enum CallOrPutEnum {
    /**
     * 看涨
     */
    call("call","Call"),
    /**
     * 看跌
     */
    put("put","Put")
    ;
    private final String key;
    private final String desc;

    CallOrPutEnum(String key,String desc) {
        this.key=key;
        this.desc = desc;
    }
    public String getKey(){
        return this.key;
    }
    public String getDesc(){
        return this.desc;
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
