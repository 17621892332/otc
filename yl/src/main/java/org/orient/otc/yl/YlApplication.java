package org.orient.otc.yl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author dzrh
 */
@SpringBootApplication(scanBasePackages = {"org.orient.otc"})
@EnableFeignClients({"org.orient.otc.api.**.feign"})
public class YlApplication {
    public static void main(String[] args) {
        SpringApplication.run(YlApplication.class);
    }
}
