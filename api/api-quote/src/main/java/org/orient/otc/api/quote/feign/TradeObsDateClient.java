package org.orient.otc.api.quote.feign;

import org.orient.otc.api.quote.dto.SettlementTradeObsDateDTO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "quoteserver",path = "/tradeObsDate", contextId ="tradeObsDate")
public interface TradeObsDateClient {

    /**
     * 通过交易日期与交易类型获取需要敲出的交易编号
     * @param dto 期权类型与敲出日期
     * @return 交易编号
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getTradeCodeList")
    List<String> getNeedKnockOutTradeCodeList(@RequestBody SettlementTradeObsDateDTO dto);
}
