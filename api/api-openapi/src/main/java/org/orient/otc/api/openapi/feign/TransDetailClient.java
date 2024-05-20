package org.orient.otc.api.openapi.feign;

import org.orient.otc.api.openapi.vo.TransDetailVo;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author pjc
 */
@FeignClient(value = "openapiserver",path = "/transDetail", contextId ="transDetail")
public interface TransDetailClient {
    /**
     * 通过客户ID获取客户列表
     * @return 客户列表
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getTransDetail")
    List<TransDetailVo> getTransDetail();
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/statusConvertY")
    Boolean statusConvertY(@RequestParam String id);
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/statusConvertN")
    Boolean statusConvertN(@RequestParam String id);
}
