package org.orient.otc.api.user.feign;

import org.orient.otc.api.user.dto.ExchangeAccountQueryDto;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(value = "userserver",path = "/exchangeAccount", contextId ="exchangeAccount")
public interface ExchangeAccountClient {
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getList")
    List<ExchangeAccountFeignVO> getList();
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVoByname")
    ExchangeAccountFeignVO getVoByname(@RequestBody ExchangeAccountQueryDto exchangeAccountQuery);

    /**
     * @param ids
     * @return
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVoByAssetUnitIds")
    List<ExchangeAccountFeignVO> getVoByAssetUnitIds(@RequestBody Set<Integer> ids);

    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVoByAccounts")
    List<ExchangeAccountFeignVO>  getVoByAccounts(@RequestBody Set<String> accounts);
}
