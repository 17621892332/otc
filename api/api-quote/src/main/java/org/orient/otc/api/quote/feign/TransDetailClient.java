package org.orient.otc.api.quote.feign;

import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "quoteserver",path = "/transDetailClient", contextId ="transDetailClient")
public interface TransDetailClient {
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getTransDetail")
    Boolean getTransDetail();
}
