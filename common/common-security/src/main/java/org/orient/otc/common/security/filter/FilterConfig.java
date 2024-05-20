package org.orient.otc.common.security.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dzrh
 */
@Configuration
public class FilterConfig {
	@Bean //将方法中返回的对象注入到IOC容器中
    public FilterRegistrationBean<RepeatlyReadFilter> filterRegister(){
        FilterRegistrationBean<RepeatlyReadFilter> reFilter = new FilterRegistrationBean<>();
        reFilter.setFilter(securityFilterBean()); //创建并注册Filter
        reFilter.addUrlPatterns("/*"); //拦截的路径（对所有请求拦截）
        reFilter.addInitParameter("exclusions","/v2/api-docs");
        reFilter.setName("RepeatlyReadFilter"); //拦截器的名称
        reFilter.setOrder(1); //拦截器的执行顺序。数字越小越先执行
        return  reFilter;
    }
    @Bean
    public RepeatlyReadFilter securityFilterBean() {
        return new RepeatlyReadFilter();
    }
}
