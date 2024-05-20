package org.orient.otc.yl.enums;

import org.apache.commons.lang3.StringUtils;

public enum ExerciseTypeEnum {
    /**
     * 欧式
     */
    european("european","European"),
    /**
     * 美式
     */
    american("american","American"),
    ;
    private final String key;
    private final String desc;

    ExerciseTypeEnum(String key,String desc) {
        this.key=key;
        this.desc = desc;
    }
    public String getKey(){
        return this.key;
    }
    public String getDesc(){
        return this.desc;
    }
    public static ExerciseTypeEnum getTradeTypeByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (ExerciseTypeEnum enums : ExerciseTypeEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
    }
    public static ExerciseTypeEnum getTradeTypeByDesc(String desc){
        if (StringUtils.isEmpty(desc)){
            return null;
        }
        for (ExerciseTypeEnum enums : ExerciseTypeEnum.values()){
            if (enums.getDesc().equals(desc)){
                return enums;
            }
        }
        return null;
    }
}
