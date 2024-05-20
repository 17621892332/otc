package org.orient.otc.yl.controller;

import org.apache.commons.lang3.StringUtils;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.yl.service.SyncServe;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@RestController
@RequestMapping("/sync")
public class SyncController {

    @Resource
    private SyncServe syncServe;

    /**
     * 拉取客户信息
     * @return 拉取结果
     */
    @GetMapping("/syncClient")
    public HttpResourceResponse<Boolean> syncClient(){
        return HttpResourceResponse.success(syncServe.syncClient());
    }

    /**
     * 同步保证金信息
     * @param clientId 客户ID
     * @param riskDate 风险日期
     * @return 同步结果
     */
    @GetMapping("/syncMargin")
    public HttpResourceResponse<?> syncMargin(@RequestParam(required = false) Integer clientId, @RequestParam LocalDate riskDate) {
        String retMsg = syncServe.syncTradeRiskMargin(clientId, riskDate);
        if (StringUtils.isBlank(retMsg)) {
            return HttpResourceResponse.success(retMsg);
        } else {
            return HttpResourceResponse.error(900001, retMsg);
        }
    }

    /**
     * 同步风险信息
     * @param clientId 客户ID
     * @param riskDate 风险日期
     * @return 同步结果
     */
    @GetMapping("/syncRiskInfo")
    public HttpResourceResponse<?> syncRiskInfo(@RequestParam(required = false) Integer clientId, @RequestParam LocalDate riskDate) {
        String retMsg = (syncServe.syncTradeRiskPv(clientId, riskDate));
        if (StringUtils.isBlank(retMsg)) {
            return HttpResourceResponse.success(retMsg);
        } else {
            return HttpResourceResponse.error(900001, retMsg);
        }
    }
}
