package org.orient.otc.api.file.enums;

import lombok.Getter;

/**
 * @author chengqiang
 */
@Getter
public enum FileTypeEnum {
    tradeConfirm("交易确认书"),
    settlementConfirm("结算确认书"),
    ;

    private final String desc;

    FileTypeEnum(String desc) {
        this.desc = desc;
    }

}
