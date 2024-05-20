package org.orient.otc.quote.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 结算报告导出枚举
 */
@Getter
public enum SettlemenReportSheetEnum {

    /**
     * 账户状况
     */
    accountOverview("账户状况"),
    /**
     * 累计汇总
     */
    accSummary("累计汇总"),
    /**
     * 资金明细
     */
    capital("资金明细"),
    /**
     * 持仓明细
     */
    tradeRisk("持仓明细"),

    /**
     * 历史交易
     */
    historyTrade("历史交易"),
    /**
     * 质押记录
     */
    collateral("质押记录"),
    ;

    private final String desc;

    SettlemenReportSheetEnum( String desc) {
        this.desc = desc;
    }

    public static SettlemenReportSheetEnum getEnumByDesc(String desc){
        if (StringUtils.isEmpty(desc)){
            return null;
        }
        for (SettlemenReportSheetEnum enums : SettlemenReportSheetEnum.values()){
            if (enums.getDesc().equals(desc)){
                return enums;
            }
        }
        return null;
    }
}
