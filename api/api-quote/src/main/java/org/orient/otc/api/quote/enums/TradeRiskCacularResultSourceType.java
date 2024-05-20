package org.orient.otc.api.quote.enums;

public enum TradeRiskCacularResultSourceType {
    exchange("场内"),
    over("场外"),
    ;
    private String desc;

    TradeRiskCacularResultSourceType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
}
