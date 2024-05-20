package org.orient.otc.yl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.orient.otc.yl.config.YlProperties.PREFIX;


/**
 * @author dzrh
 */
@Data
@Component
@ConfigurationProperties(PREFIX)
public class YlProperties {

    public static final String PREFIX = "yl";


    private String account;

    private String password;

    /**
     * url
     */
    private String url;
}
