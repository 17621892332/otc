package org.orient.otc.api.finoview.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class FinoviewFeignConfig implements RequestInterceptor {
    /*
    appkey:
D211ED8F38713301776DB85CE92C9979
appsecret:
18b0224cd1717374cf5c3c8104249310
     */
    @Value("${finoview.username}")
    private String username;

    @Value("${finoview.password}")
    private String password;

    @Override
    public void apply(RequestTemplate template) {
        //只有内部接口才添加请求头->给feign请求添加请求头

        template.header("username",username);
        template.header("password",password);

    }
}

