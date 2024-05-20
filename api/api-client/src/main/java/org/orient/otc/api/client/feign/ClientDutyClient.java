package org.orient.otc.api.client.feign;

import org.orient.otc.api.client.vo.ClientDutyVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = "clientserver",path = "/clientDuty", contextId ="clientDuty")
public interface ClientDutyClient {

    /**
     * 根据客户ID查询客户联系人列表
     * @param clientId 客户ID
     * @return 返回联系人列表
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientDutyByClientId")
    List<ClientDutyVO> getClientDutyByClientId(@RequestParam Integer clientId);

    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getMapByClientId")
    Map<String, Set<String>> getMapByClientId(@RequestParam String id);
}
