package org.orient.otc.netty;

import org.orient.otc.netty.component.WebsocketApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(scanBasePackages = {"org.orient.otc"})
@EnableFeignClients({"org.orient.otc.api.**.feign"})
public class NettyApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(NettyApplication.class, args);
        WebsocketApplication websocketApplication = ctx.getBean("WebsocketApplication", 		WebsocketApplication.class);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(websocketApplication);
        service.shutdown();
    }
}
