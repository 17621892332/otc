package org.orient.otc.quote.feign;

import org.orient.otc.api.quote.dto.SettlementTradeObsDateDTO;
import org.orient.otc.api.quote.feign.TradeObsDateClient;
import org.orient.otc.quote.service.TradeObsDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/tradeObsDate")
public class TradeObsDateFeignController implements TradeObsDateClient {
    @Autowired
    private TradeObsDateService  tradeObsDateService;

    @Override
    public List<String> getNeedKnockOutTradeCodeList(SettlementTradeObsDateDTO dto) {
        return tradeObsDateService.getNeedKnockOutTradeCodeList(dto);
    }
}
