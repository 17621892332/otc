package org.orient.otc.common.core.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        //只有内部接口才添加请求头->给feign请求添加请求头
        if (template.url().contains(FeignConfig.FEIGN_INSIDE_URL_PREFIX)) {
            template.header(FeignHeader.REQUEAST_HEADER.getKey(), FeignHeader.REQUEAST_HEADER.getValue());

            //传递用户token
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes == null) {
                return;
            }
            //传递用户token信息
            HttpServletRequest request = attributes.getRequest();
            String authorization = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(authorization)) {
                template.header("Authorization", authorization);
            }
        }
    }
}

