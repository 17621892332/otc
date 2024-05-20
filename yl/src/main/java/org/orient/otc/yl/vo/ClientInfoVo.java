package org.orient.otc.yl.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientInfoVo {
    /**
     *  "ClientId": 1,
     *             "ClientName": "建发（上海）有限公司",
     *             "ClientNumber": "JFSH",
     *             "ClientType": "机构",
     *             "LicenseCode": "9131011573978005X5",
     *             "LicenseType": "营业执照",
     *             "CreditStartDate": "2022-06-15T00:00:00",
     *             "CreditDeadLine": "2023-06-14T00:00:00"
     */
    Integer clientId;
    /**
     * 客户名称
     */
    String clientName;
    /**
     * 客户代码
     */
    String clientNumber;
    /**
     * 客户类型
     */
    String clientType;
    /**
     * 证件代码
     */
    String licenseCode;
    /**
     * 证件类型
     */
    String licenseType;

    LocalDateTime creditStartDate;

    LocalDateTime creditDeadLine;
}
