package org.orient.otc.quote.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateAddDTO;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateDeleteDTO;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateSelectDTO;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateUpdateDTO;
import org.orient.otc.quote.entity.ScenarioTemplate;
import org.orient.otc.quote.service.ScenarioTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/scenario")
@Api(tags = "情景分析请求参数")
public class ScenarioTemplateController {
    @Autowired
    ScenarioTemplateService scenarioTemplateService;

    @PostMapping("/save")
    @ApiOperation("保存情景分析请求参数")
    public HttpResourceResponse<String> save(@RequestBody @Valid ScenarioTemplateAddDTO dto){
        return HttpResourceResponse.success(scenarioTemplateService.save(dto));
    }

    @PostMapping("/getById")
    @ApiOperation("根据id获取情景分析请求参数")
    public HttpResourceResponse<ScenarioTemplate> getById(@RequestBody @Valid ScenarioTemplateSelectDTO dto){
        return HttpResourceResponse.success(scenarioTemplateService.getById(dto));
    }

    @PostMapping("/delete")
    @ApiOperation("根据id删除情景分析请求参数")
    public HttpResourceResponse<String> delete(@RequestBody @Valid ScenarioTemplateDeleteDTO dto){
        return HttpResourceResponse.success(scenarioTemplateService.delete(dto));
    }

    @PostMapping("/list")
    @ApiOperation("获取情景分析请求参数列表")
    public HttpResourceResponse<List<ScenarioTemplate>> selectByList(){
        return HttpResourceResponse.success(scenarioTemplateService.selectByList());
    }
}
