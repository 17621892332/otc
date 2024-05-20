package org.orient.otc.openapi.vo;

import lombok.Data;

@Data
public class AppToken {
    private String appId;
    private String appSecret;
    private String tenantid;
    private String accountId;
}
