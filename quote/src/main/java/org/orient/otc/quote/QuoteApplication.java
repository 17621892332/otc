package org.orient.otc.quote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启动类
 */
@EnableAsync
@SpringBootApplication(scanBasePackages = {"org.orient.otc"},exclude = {DataSourceAutoConfiguration.class,MultipartAutoConfiguration.class})
@EnableFeignClients({"org.orient.otc.api.**.feign"})
public class QuoteApplication {
    /**
     * 启动方法
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(QuoteApplication.class);
    }
}
