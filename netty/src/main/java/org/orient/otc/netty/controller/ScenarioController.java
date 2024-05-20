package org.orient.otc.netty.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.netty.dto.RiskInfoQueryByPageDto;
import org.orient.otc.api.quote.dto.risk.TradeRiskCacularResult;
import org.orient.otc.api.vo.BucketedVegaVO;
import org.orient.otc.api.vo.ScenarioQuoteVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.netty.dto.ScenarioQuoteDTO;
import org.orient.otc.netty.service.RiskService;
import org.orient.otc.netty.service.ScenarioService;
import org.orient.otc.netty.vo.UnderlyingVarietyVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 情景分析
 */
@RestController
@RequestMapping("/scenario")
@Api(tags = "情景分析")
@Validated
public class ScenarioController {

    @Resource
    private RiskService riskService;

    @Resource
    private ScenarioService scenarioService;
    /**
     * 获取所有交易信息
     * @param dto  请求参数
     * @return 存续交易
     */
    @ApiOperation("获取交易信息")
    @PostMapping("getTradeList")
    public HttpResourceResponse<Page<TradeRiskCacularResult>> getTradeList(@RequestBody RiskInfoQueryByPageDto dto) {
        /*List<TradeRiskCacularResult> resultList= riskService.getTradeRiskCacularResult(dto);
        resultList.subList(0, Math.min(resultList.size(), 100));*/
        return HttpResourceResponse.success(riskService.getTradeListByPage(dto));
    }

    /**
     * 计算
     * @param scenarioDTO 情景分析参数
     * @return 计算结果
     */
    @PostMapping("/quote")
    public HttpResourceResponse<List<ScenarioQuoteVO>> quote(@RequestBody  @Valid ScenarioQuoteDTO scenarioDTO){
        return HttpResourceResponse.success(scenarioService.scenario(scenarioDTO));
    }

    /**
     * 计算
     * @param scenarioDTO 情景分析参数
     * @return 计算结果
     */
    @PostMapping("/bucketedVega")
    public HttpResourceResponse<List<BucketedVegaVO>> bucketedVega(@RequestBody ScenarioQuoteDTO scenarioDTO){
        return HttpResourceResponse.success(scenarioService.bucketedVega(scenarioDTO));
    }

    /**
     * 获取标的品种列表
     * @return 标的品种
     */
    @GetMapping("/getUnderlyingVarietyList")
    public HttpResourceResponse<List<UnderlyingVarietyVO>> getUnderlyingVarietyList(){
        return HttpResourceResponse.success(scenarioService.getUnderlyingVarietyList());
    }

}
