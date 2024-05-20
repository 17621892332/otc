package org.orient.otc.api.system.feign;

import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author dzrh
 */
@FeignClient(value = "systemserver",path = "/dictionary", contextId ="dictionary")
public interface DictionaryClient {

    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getDictionaryMapByIds")
    Map<String,String> getDictionaryMapByIds(@RequestParam String dictTypeCode);

    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getMap")
    Map<String, String> getDictionaryMap();
}
