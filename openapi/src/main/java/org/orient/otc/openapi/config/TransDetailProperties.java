package org.orient.otc.openapi.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.orient.otc.openapi.config.TransDetailProperties.PREFIX;
/**
 * @author dzrh
 */
@Data
@Component
@ConfigurationProperties(PREFIX)
public class TransDetailProperties {

    public static final String PREFIX = "td";

    private String appId;
    private String appSecret;
    private String tenantid;
    private String accountId;
    private int pagesize;
    private String usertype;
    private String language;
    private String user;
    private String url;
}
