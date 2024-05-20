package org.orient.otc.api.message.enums;

import lombok.Getter;

/**
 * @author chengqiang
 */
@Getter
public enum MailTypeEnum {
    settleReport("settleReport", "结算报告"),
    capitalMonitor("capitalMonitor", "资金监控"),
    settleReportResend("settleReportResend", "结算报告重发"),
    capitalMonitorResend("capitalMonitorResend", "资金监控重发"),
    back("back", "退回"),
    other("other", "其他"),
    ;
    private final String key;
    private final String desc;

    MailTypeEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
    public static MailTypeEnum getByKey(String key) {
        for (MailTypeEnum e : MailTypeEnum.values()) {
            boolean flag = e.getKey().equals(key);
            if (flag){
                return e;
            }
        }
        return null;
    }
}
