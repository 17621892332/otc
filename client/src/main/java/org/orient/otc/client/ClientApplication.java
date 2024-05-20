package org.orient.otc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"org.orient.otc"})
@EnableFeignClients({"org.orient.otc.api.**.feign"})
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class);
    }
}
