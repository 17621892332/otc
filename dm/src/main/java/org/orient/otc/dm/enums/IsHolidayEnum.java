package org.orient.otc.dm.enums;

public enum IsHolidayEnum {
    weekday("工作日"),
    holiday("假日");

    private String desc;

    IsHolidayEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
