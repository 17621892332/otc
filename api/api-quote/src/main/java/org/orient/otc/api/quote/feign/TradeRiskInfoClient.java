package org.orient.otc.api.quote.feign;

import org.orient.otc.api.quote.vo.TradeRiskPVInfoVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * 日终持仓风险信息
 * @author dzrh
 */
@FeignClient(value = "quoteserver",path = "/tradeRiskInfo", contextId ="tradeRiskInfo")
public interface TradeRiskInfoClient {

    /**
     * 通过日期获取交易持仓保证金
     * @param clientId 客户Id
     * @param riskDate 交易日期
     * @return 保证金数据
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getRiskInfoListByRiskDate")
    List<TradeRiskPVInfoVO> getRiskInfoListByRiskDate(@RequestParam(required = false) Integer clientId, @RequestParam LocalDate riskDate);

}
