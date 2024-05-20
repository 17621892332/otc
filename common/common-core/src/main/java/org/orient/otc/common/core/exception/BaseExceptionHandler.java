package org.orient.otc.common.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.common.core.feign.FeignHeader;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    /**
     * 基础异常
     * @param e 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public HttpResourceResponse<?> handleBaseException(Exception e) {
        log.error(e.getMessage(), e);
        return HttpResourceResponse.error(500,e.toString(), "系统异常,请联系管理员");
    }

    @ExceptionHandler(CompletionException.class)
    @ResponseBody
    public HttpResourceResponse<?> handleCompletionException(CompletionException e) {
        log.error(e.getMessage(), e);
        return HttpResourceResponse.error(500,e.toString(),e.toString());
    }
    /**
     * 业务异常
     * @param baseException 业务对象
     * @return 响应结果
     */
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public HttpResourceResponse<?> handleBaseException(BaseException baseException){
        HttpServletRequest request;
        request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        boolean isFeign = StringUtils.isNotBlank(request.getHeader(FeignHeader.REQUEAST_HEADER.getKey()))
                && request.getHeader(FeignHeader.REQUEAST_HEADER.getKey()).equals(FeignHeader.REQUEAST_HEADER.getValue());
        //如果是feignclien请求,不用对返回参数进行HttpResourceResponse包装
        if(isFeign) {
            throw baseException;
        }
        return HttpResourceResponse.error(baseException);
    }


    /**
     * 处理@Valid所引发的，参数校验失败引发的【MethodArgumentNotValidException】异常；
     * @param e 参数异常
     * @return 响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public HttpResourceResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException:", e);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return HttpResourceResponse.error(500,message);
    }
}
