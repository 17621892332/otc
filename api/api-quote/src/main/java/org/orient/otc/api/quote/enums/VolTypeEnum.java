package org.orient.otc.api.quote.enums;

import org.apache.commons.lang3.StringUtils;

public enum VolTypeEnum {
    mid("mid","交易"),
    bid("bid","报价Bid"),
    ask("ask","报价Ask");
    private final String key;
    private final String desc;

    VolTypeEnum(String key,String desc) {
        this.key=key;
        this.desc = desc;
    }
    public String getKey(){
        return this.key;
    }
    public String getDesc(){
        return this.desc;
    }

    public static VolTypeEnum getTradeTypeByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (VolTypeEnum enums : VolTypeEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
    }
    public static VolTypeEnum getTradeTypeByDesc(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (VolTypeEnum enums : VolTypeEnum.values()){
            if (enums.getDesc().equals(key)){
                return enums;
            }
        }
        return null;
    }
}
