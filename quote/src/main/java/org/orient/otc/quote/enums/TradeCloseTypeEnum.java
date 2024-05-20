package org.orient.otc.quote.enums;

/**
 * 平仓类型
 */
public enum TradeCloseTypeEnum {

    /**
     * 平仓
     */
    close("平仓"),
    /**
     * 到期执行
     */
    execute("到期执行"),
    /**
     * 敲出终止
     */
    knockoutTerminate("敲出终止"),
            ;
    private String desc;

    TradeCloseTypeEnum(String desc) {
        this.desc = desc;
    }
}
