package org.orient.otc.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = {"org.orient.otc"})
@EnableFeignClients({"org.orient.otc.api.**.feign"})
@EnableAsync
public class MessageApplication {
    /**
     * 启动方法
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class);
    }
}
