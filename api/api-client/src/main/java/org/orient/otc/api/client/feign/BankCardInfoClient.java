package org.orient.otc.api.client.feign;

import org.orient.otc.api.client.vo.BankCardInfoVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "clientserver",path = "/bankCardInfoClient", contextId ="bankCardInfoClient")
public interface BankCardInfoClient {
    @GetMapping (FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getByClientId")
    List<BankCardInfoVO> getBankCardInfoByClientId(@RequestParam Integer id);
    @GetMapping (FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientIdByBankAccount")
    Integer getClientIdByBankAccount(@RequestParam String bankAccount);
}
