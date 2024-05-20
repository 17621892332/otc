package org.orient.otc.api.dm.feign;

import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(value = "dmserver",path = "/instrument", contextId ="instrument")
public interface InstrumentClient {
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getInstrumentInfo")
    InstrumentInfoVo getInstrumentInfo(@RequestParam String instID);
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateExchangeInstrument")
    Boolean updateExchangeInstrument();

    /**
     * 通过合约代码获取合约信息
     * @param instIDs
     * @return
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getInstrumentInfoByIds")
    List<InstrumentInfoVo> getInstrumentInfoByIds(@RequestParam Set<String> instIDs);

    /**
     * 通过标的代码获取合约信息
     * @param codes
     * @return
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getInstrumentInfoByUndeingCodes")
    List<InstrumentInfoVo> getInstrumentInfoByUndeingCodes(@RequestParam Set<String> codes);

}
