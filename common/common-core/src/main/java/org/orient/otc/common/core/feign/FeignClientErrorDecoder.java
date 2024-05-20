package org.orient.otc.common.core.feign;

import com.alibaba.fastjson.JSONObject;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.core.exception.BaseException;
import org.orient.otc.common.core.exception.CommonExceptionEnum;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

/**
 * @Description 自定义错误解码器
 **/

@Slf4j
@Configuration
public class FeignClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        try {
            if (response.body() != null) {
                //会把异常信息转换成字符串，注意断点不要打在这一行，会报IO异常
                //断点可以打在它的下一行
                String body = Util.toString(response.body().asReader(Charset.defaultCharset()));
                log.error(body);
                //将字符串转换为自定义的异常信息
                ExceptionInfo e = JSONObject.parseObject(body,ExceptionInfo.class);
                //返回异常信息，随便返回哪个异常都行，主要是将code和message透传到前端
                CommonExceptionEnum.SERVICE_ERROR.doThrow(e.getPath()+":"+e.getMessage());
            }
        } catch (Exception e) {
            log.warn("Feign异常处理错误：", e);
            return e;
        }
        //默认返回"系统异常,请稍后重试"
        return new BaseException(CommonExceptionEnum.WAIT_RETRY);
    }
}
