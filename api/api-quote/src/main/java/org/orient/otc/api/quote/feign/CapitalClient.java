package org.orient.otc.api.quote.feign;

import org.orient.otc.api.quote.dto.CapitalSyncDTO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "quoteserver",path = "/capital", contextId ="capital")
public interface CapitalClient {
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateSync")
    Boolean updateSync(@RequestBody CapitalSyncDTO capitalSyncDTO);
}
