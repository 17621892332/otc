package org.orient.otc.api.quote.vo;

import lombok.Data;

@Data
public class SettlementReportFileVO {
    /**
     * 结算报告附件
     */
    byte[] settlementReportTempFileByte;
    /**
     * 报告文件名称 , 仅在结算报告页面发送邮件使用此参数
     */
    private String tempFileName;
}
