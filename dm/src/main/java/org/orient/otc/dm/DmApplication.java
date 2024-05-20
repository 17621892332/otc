package org.orient.otc.dm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"org.orient.otc"})
@EnableFeignClients({"org.orient.otc.api.**.feign"})
public class DmApplication {
    public static void main(String[] args) {
        SpringApplication.run(DmApplication.class);
    }
}