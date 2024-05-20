package org.orient.otc.quote.feign;

import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.quote.feign.TradeRiskInfoClient;
import org.orient.otc.api.quote.vo.TradeRiskPVInfoVO;
import org.orient.otc.quote.service.TradeRiskInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @author pjc
 */
@RestController
@RequestMapping(value = "tradeRiskInfo")
@Slf4j
public class TradeRiskInfoFeignController implements TradeRiskInfoClient {

    @Resource
    private TradeRiskInfoService tradeRiskInfoService;

    @Override
    public List<TradeRiskPVInfoVO> getRiskInfoListByRiskDate(Integer clientId, LocalDate riskDate) {
        return tradeRiskInfoService.getRiskInfoListByRiskDate(clientId,riskDate);
    }
}
