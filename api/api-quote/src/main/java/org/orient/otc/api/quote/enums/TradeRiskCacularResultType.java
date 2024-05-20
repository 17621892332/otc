package org.orient.otc.api.quote.enums;

public enum TradeRiskCacularResultType {
    option("期权"),
    european("期货"),
    ;
    private String desc;

    TradeRiskCacularResultType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
}
