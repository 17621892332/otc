package org.orient.otc.api.dm.feign;

import org.orient.otc.api.dm.vo.UnderlyingQuoteVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "dmserver",path = "/underlyingQuote", contextId ="underlyingQuote")
public interface UnderlyingQuoteClient {


    /**
     * 通过合约代码获取合约信息
     * @return 合约信息
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUnderlyingQuoteList")
    List<UnderlyingQuoteVO> getUnderlyingQuoteList();
}
