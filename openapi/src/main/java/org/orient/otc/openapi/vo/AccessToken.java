package org.orient.otc.openapi.vo;

import lombok.Data;

@Data
public class AccessToken {
    private String user;
    private String apptoken;
    private String tenantid;
    private String accountId;
    private String usertype;
    private String language;
}
