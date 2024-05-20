package org.orient.otc.quote.enums;

public enum ExchangeEodType {
    /**
     * 多头为买入
     */
    LONG("多头"),
    /**
     * 空头为卖出
     */
    SHORT("空头"),
    ;
    private String desc;

    ExchangeEodType(String desc) {
        this.desc = desc;
    }
}
