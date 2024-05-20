package org.orient.otc.quote.vo.settlementreport;

import lombok.Data;

@Data
public class SettlementReportExcelVO {
    private String endDate;
    private String clientCode;
    private String clientName;
    private String startDateFormat;
    private String endDateFormat;
    private String nowDateFormat;
    private String nowDateFormatChina;
    private String buildReportUser;
    private AccountOverviewVO accountOverviewVO;
}
