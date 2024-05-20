package org.orient.otc.common.redisstream.enums;

public enum StreamTypeEnum {
    simple("推的模式"),
    pull("主动拉模式"),
    ;
    private String desc;

    StreamTypeEnum(String desc) {
        this.desc = desc;
    }
}
