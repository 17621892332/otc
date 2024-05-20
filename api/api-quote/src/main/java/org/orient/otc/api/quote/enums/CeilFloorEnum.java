package org.orient.otc.api.quote.enums;

import lombok.Getter;

/**
 * 封顶保底
 */
@Getter
public enum CeilFloorEnum {
    /**
     * 封顶
     */
    ceil("封顶"),
    /**
     * 保底
     */
    floor("保底")
    ;

    private final String desc;

    CeilFloorEnum( String desc) {

        this.desc = desc;
    }

}
