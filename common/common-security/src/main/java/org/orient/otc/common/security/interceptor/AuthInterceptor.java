package org.orient.otc.common.security.interceptor;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.core.adapter.CommonConfig;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.feign.FeignHeader;
import org.orient.otc.common.security.adapter.AuthAdapter;
import org.orient.otc.common.security.annotion.NoCheckLogin;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.exception.BussinessException;
import org.orient.otc.common.security.util.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
