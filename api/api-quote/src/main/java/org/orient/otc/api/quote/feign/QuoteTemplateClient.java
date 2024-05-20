package org.orient.otc.api.quote.feign;

import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 模板内部接口
 */
@FeignClient(value = "quoteserver",path = "/quoteTemplate", contextId ="quoteTemplate")
public interface QuoteTemplateClient {
    /**
     * 删除到期模板
     * @return 是否成功
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/deleteMaturityTemplate")
    Boolean deleteMaturityTemplate();
}
