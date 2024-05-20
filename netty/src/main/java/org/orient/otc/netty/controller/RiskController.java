package org.orient.otc.netty.controller;

import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.netty.dto.RiskInfoQueryDto;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.netty.service.RiskService;
import org.orient.otc.netty.vo.RiskTotalVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author dzrh
 */
@RestController
@RequestMapping("/risk")
public class RiskController {
    @Resource
    RiskService riskService;

    @ApiOperation("获取所有风险信息")
    @PostMapping("getRiskInfoList")
    public HttpResourceResponse<RiskTotalVo> getRiskInfoList(@RequestBody RiskInfoQueryDto dto) {
        return HttpResourceResponse.success(riskService.getRiskInfoList(dto));
    }


    @ApiOperation("获取客户列表")
    @PostMapping("getClientList")
    public HttpResourceResponse<List<ClientVO>> getClientList(){
        return HttpResourceResponse.success(riskService.getClientList());
    }
    @ApiOperation("获取品种列表")
    @PostMapping("getVarietyList")
    public HttpResourceResponse<Set<String>> getVarietyList(){
        return HttpResourceResponse.success(riskService.getVarietyList());
    }
}


