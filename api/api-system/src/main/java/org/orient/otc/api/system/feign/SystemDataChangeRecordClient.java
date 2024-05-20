package org.orient.otc.api.system.feign;

import org.orient.otc.api.system.dto.APICapitalDataChangeRecordAddDto;
import org.orient.otc.api.system.dto.APIGrantCreditDataChangeRecordAddDto;
import org.orient.otc.api.system.dto.clientdatachangerecord.APIClientDataChangeRecordAddDto;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordDTO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author dzrh
 */
@FeignClient(value = "systemserver",path = "/systemDataChangeRecord", contextId ="systemDataChangeRecord")
public interface SystemDataChangeRecordClient {

    /**
     * 新增交易变更记录
     * @param tradeDataChangeRecordDTO  变更对象
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/addTradeDataChangeRecord")
    void addTradeDataChangeRecord(@RequestBody TradeDataChangeRecordDTO tradeDataChangeRecordDTO);
    /**
     * 新增客户变更记录
     * @param dto  变更对象
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/addClientDataChangeRecord")
    void addClientDataChangeRecord(@RequestBody APIClientDataChangeRecordAddDto dto);

    /**
     * 新增资金变更记录
     * @param dto  变更对象
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/addCapitalDataChangeRecord")
    void addCapitalDataChangeRecord(@RequestBody APICapitalDataChangeRecordAddDto dto);

    /**
     * 新增授信变更记录
     * @param dto  变更对象
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/addGrantCreditDataChangeRecord")
    void addGrantCreditDataChangeRecord(@RequestBody APIGrantCreditDataChangeRecordAddDto dto);
}
