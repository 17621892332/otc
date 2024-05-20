package org.orient.otc.api.client.feign;

import org.orient.otc.api.client.dto.GrantCreditDTO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * 授信额度接口
 */
@FeignClient(value = "clientserver",path = "/grantCredit", contextId ="grantCredit")
public interface GrantCreditClient {

    /**
     * 获取客户授信额度
     * @param grantCreditDTO 请求参数
     * @return 授信额度
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientGrantCredit")
    Map<Integer, BigDecimal> getClientGrantCredit(@RequestBody GrantCreditDTO grantCreditDTO);
}
