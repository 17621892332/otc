package org.orient.otc.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ConfigBean {

    @Value("${spring.profiles.active}")
    private String active;

    @Bean
    private String profileActive(){
        return active;
    }
}
