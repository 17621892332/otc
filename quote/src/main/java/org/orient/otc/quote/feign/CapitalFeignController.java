package org.orient.otc.quote.feign;

import org.orient.otc.api.quote.dto.CapitalSyncDTO;
import org.orient.otc.api.quote.feign.CapitalClient;
import org.orient.otc.quote.service.CapitalRecordsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 资金记录同步状态
 */
@RestController
@RequestMapping(value = "/capital")
public class CapitalFeignController implements CapitalClient {
    @Resource
    private CapitalRecordsService capitalRecordsService;


    @Override
    public Boolean updateSync(CapitalSyncDTO capitalSyncDTO) {
        return capitalRecordsService.capitalUpdateSync(capitalSyncDTO);
    }
}
