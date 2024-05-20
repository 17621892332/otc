package org.orient.otc.api.dm.feign;

import org.orient.otc.api.dm.vo.VarietyVo;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

/**
 * @author dzrh
 */
@FeignClient(value = "dmserver",path = "/variety", contextId ="variety")
public interface VarietyClient {
    /**
     * 获取品种代码Map
     * @return key 品种ID  value 品种code
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVarietyMap")
    Map<String,Integer> getVarietyMap();

    /**
     * 通过ID获取品种信息
     * @param varietyId 品种ID
     * @return 品种信息
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVarietyById")
    VarietyVo getVarietyById(@RequestParam Integer varietyId);

    /**
     * 获取品种代码Map
     * @return key 品种ID  value 品种名称
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVarietyNameMap")
    Map<Integer,String> getVarietyNameMap();

    /**
     * 获取品种对应的产业链
     * @param idSet 品种ID
     * @return key 品种ID value 产业链名称
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVarietyTypeNameMap")
    Map<Integer,String> getVarietyTypeNameMap(@RequestBody Set<Integer> idSet);
}
