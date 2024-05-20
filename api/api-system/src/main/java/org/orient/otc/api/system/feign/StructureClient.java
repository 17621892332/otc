package org.orient.otc.api.system.feign;

import org.orient.otc.api.system.vo.StructureInfoVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 自定义结构期权
 */
@FeignClient(value = "systemserver",path = "/structure", contextId ="structure")
public interface StructureClient {
    /**
     * 获取自定义结构列表
     * @return 结构列表
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getStructureInfoList")
    List<StructureInfoVO> getStructureInfoList();
}
