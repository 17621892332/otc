package org.orient.otc.openapi.vo;

import java.time.LocalDateTime;

@lombok.Data
public class Data {
    private String app_token;
    private String access_token;
    private boolean success;
    private String error_desc;
    private LocalDateTime expire_time;
    private String error_code;
}