package org.orient.otc.common.security.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.HttpMethod;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.orient.otc.common.core.feign.FeignConfig;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.dto.SystemLogInfo;
import org.orient.otc.common.security.util.AdrressIpUtils;
import org.orient.otc.common.security.util.ThreadContext;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 记录所有的请求日志
 * @author 潘俊材
 */
@Slf4j
public class RepeatlyReadFilter implements Filter {

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Value("${spring.application.name}")
    private String serverName;

    @Value("${logIsSendRocketMQ:true}")
    private Boolean logIsSendRocketMQ;
    /**
     * form-data格式：不包含文件
     */
    private static final String FORM_DATA = "application/x-www-form-urlencoded";
    /**
     * form-data格式：包含文件
     */
    private static final String FORM_DATA_FILE = "multipart/form-data";
    /**
     * application/json 格式
     */
    private static final String APPLICATION_JSON = "application/json";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //内部调用不做处理
        if (req.getRequestURI().contains(FeignConfig.FEIGN_INSIDE_URL_PREFIX)) {
            chain.doFilter(req, res);
            //清空线程的缓存,防止脏读
            ThreadContext.deleteAll();
        } else {
            WrappedHttpServletRequest wrapperRequest = new WrappedHttpServletRequest((HttpServletRequest) request);
            //转换成代理类
            ResponseWrapper wrapperResponse = new ResponseWrapper((HttpServletResponse) response);
            //记录请求时间
            long startTime = System.currentTimeMillis();
            String ip = AdrressIpUtils.getIpAdrress(wrapperRequest);
            //输出请求日志
            String requestString = getRequestData(wrapperRequest);
            log.info("请求接口路径:{}\n请求内容：{}", wrapperRequest.getRequestURI(), requestString);

            chain.doFilter(wrapperRequest, wrapperResponse);

            //请求耗时
            long spendTimes = System.currentTimeMillis() - startTime;
            AuthorizeInfo authorizeInfo = ThreadContext.getAuthorizeInfo();
            SystemLogInfo logInfo = SystemLogInfo.builder()
                    .path(wrapperRequest.getRequestURI())
                    .requestInfo(requestString)
                    .serverName(serverName)
                    .ip(ip)
                    .spendTimes(spendTimes)
                    .requestTime(LocalDateTime.now())
                    .build();
            if (authorizeInfo != null) {
                logInfo.setRequestUserId(authorizeInfo.getId());
                logInfo.setRequestUserName(authorizeInfo.getName());
            }
            //获取返回值
            byte[] content = wrapperResponse.getContent();
            //把返回值输出到客户端
            ServletOutputStream out = response.getOutputStream();
            out.write(content);
            out.flush();
            out.close();
            if (content.length > 0 && wrapperResponse.getContentType().equals(APPLICATION_JSON)) {
                String responseData = IOUtils.toString(content, "UTF-8");
                //当响应过大的时候去掉data内容
                if (responseData.length() > 8000) {
                    JSONObject jsonObject = JSONObject.parseObject(responseData);
                    jsonObject.remove("data");
                    responseData = jsonObject.toJSONString();
                }
                logInfo.setResponseData(responseData);
            }
            //清空线程的缓存,防止脏读
            ThreadContext.deleteAll();
            if (logIsSendRocketMQ) {
                rocketMQTemplate.syncSend(RocketMqConstant.SYSTEM_LOG + ":" + RocketMqConstant.SYSTEM_LOG, logInfo);
            }
        }
    }

    @Override
    public void destroy() {

    }

    private String getRequestData(HttpServletRequest request) throws IOException {
        // 请求格式
        String contentType = request.getContentType();
        String requestData = "";
        String method = request.getMethod();
        // 该接口请求参数格式为application/json
        if (HttpMethod.POST.equals(method) && StringUtils.isNotBlank(contentType) && contentType.contains(APPLICATION_JSON)) {
            ServletInputStream reader = request.getInputStream();
            requestData = IOUtils.toString(reader, StandardCharsets.UTF_8);
            reader.close();
        }
        // 该接口请求参数格式为multipart/form-data
//        if (HttpMethod.POST.equals(method) && StringUtils.isNotBlank(contentType) && contentType.contains(FORM_DATA_FILE)) {
//            ServletInputStream reader = request.getInputStream();
//            Enumeration<String> parameterNames = request.getParameterNames();
//            requestData = IOUtils.toString(reader, StandardCharsets.UTF_8);
//            reader.close();
//        }
        //该接口请求参数格式为GET时
        if (HttpMethod.GET.equals(method)) {
            Map<String, String[]> params = request.getParameterMap();
            requestData = JSON.toJSONString(params);
        }
        if (StringUtil.isEmpty(requestData)) {
            return "{}";
        }
        return requestData;
    }
}
