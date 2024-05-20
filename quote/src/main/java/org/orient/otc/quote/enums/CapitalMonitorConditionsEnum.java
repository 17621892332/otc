package org.orient.otc.quote.enums;

/**
 * 资金监控预计条件
 */
public enum CapitalMonitorConditionsEnum {

    /**
     * 需要追保
     */
    callsMargin,

    /**
     * 无持仓有欠款
     */
    callsMoney,
    /**
     * 当日有持仓
     */
    havaPosition,
    /**
     * 占用授信
     */
    userGrantCredit,
    ;

    CapitalMonitorConditionsEnum() {
    }
}
