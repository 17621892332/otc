package org.orient.otc.quote.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.api.quote.dto.risk.RiskMarkDto;
import org.orient.otc.quote.entity.RiskMark;
import org.orient.otc.quote.service.RiskMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/risk/mark")
@Api(tags = "风险合约行情")
public class RiskMarkController {
    @Autowired
    RiskMarkService riskMarkService;

    @PostMapping("/insertRiskMark")
    @ApiOperation("设置合约行情")
    public HttpResourceResponse<String> insertRiskMark(@RequestBody @Valid RiskMark riskMark) {
        return HttpResourceResponse.success(riskMarkService.insertRiskMark(riskMark));
    }

    @PostMapping("/deleteRiskMark")
    @ApiOperation("清除设置合约行情")
    public HttpResourceResponse<String> deleteRiskMark(@RequestBody @Valid RiskMarkDto riskMarkDto) {
        return HttpResourceResponse.success(riskMarkService.deleteRiskMark(riskMarkDto));
    }
}
