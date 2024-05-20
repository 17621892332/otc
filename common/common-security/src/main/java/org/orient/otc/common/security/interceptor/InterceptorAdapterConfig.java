package org.orient.otc.common.security.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author dzrh
 */
@Component
public class InterceptorAdapterConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public InterceptorAdapterConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry)

    {
        //注册自己的拦截器并设置拦截的请求路径
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/v2/**","/v3/**").order(1);
    }


}
