package org.orient.otc.api.quote.feign;

import org.orient.otc.api.quote.dto.BuildSettlementReportDTO;
import org.orient.otc.api.quote.vo.SettlementReportFileVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 结算相关API
 * @author pjc
 */
@FeignClient(value = "quoteserver",path = "/settlementReport", contextId ="settlementReport")
public interface SettlementReportClient {
    /**
     * 获取某个客户的结算报告模板
     * @param dto 入参
     * @return 返回
     */
    @PostMapping(value=FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getSettlementReportTempFileByClient")
    SettlementReportFileVO getSettlementReportTempFileByClient(@RequestBody BuildSettlementReportDTO dto);
}
