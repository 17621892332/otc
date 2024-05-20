package org.orient.otc.dm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.dm.vo.VarietyVo;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.dm.dto.variety.VarietyAddDto;
import org.orient.otc.dm.dto.variety.VarietyEditDto;
import org.orient.otc.dm.dto.variety.VarietyIdDto;
import org.orient.otc.dm.dto.variety.VarietyQueryDto;
import org.orient.otc.dm.entity.Variety;
import org.orient.otc.dm.service.VarietyService;
import org.orient.otc.dm.vo.VarietyByTraderIdVO;
import org.orient.otc.dm.vo.VarietyByVarietyTypeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/variety")
@Api(tags = "标的品种")
public class VarietyController {
    @Autowired
    private VarietyService varietyService;

    @ApiOperation("获取标的品种列表")
    @PostMapping("/list")
    public HttpResourceResponse<List<Variety>> getList(){
        return HttpResourceResponse.success(varietyService.getList());
    }


    @ApiOperation("分页获取标的品种列表")
    @PostMapping("/page")
    public HttpResourceResponse<Page<VarietyVo>> page(@RequestBody VarietyQueryDto varietyQueryDto){
        return HttpResourceResponse.success(varietyService.queryVarietyList(varietyQueryDto));
    }

    @ApiOperation("新增品种")
    @PostMapping("/add")
    public HttpResourceResponse<String> add(@RequestBody VarietyAddDto varietyAddDto){
        return HttpResourceResponse.success(varietyService.addVariety(varietyAddDto));
    }

    @ApiOperation("修改品种")
    @PostMapping("/edit")
    public HttpResourceResponse<String> edit(@RequestBody VarietyEditDto varietyEditDto){
        return HttpResourceResponse.success(varietyService.editVariety(varietyEditDto));
    }

    @ApiOperation("删除品种")
    @PostMapping("/delete")
    public HttpResourceResponse<String> delete(@RequestBody VarietyIdDto varietyIdDto){

        return HttpResourceResponse.success(varietyService.deleteVariety(varietyIdDto));
    }

    /**
     * 获取产业链列表
     * @return 产业链列表
     */
    @ApiOperation(value = "获取产业链列表")
    @PostMapping("/getVarietyByVarietyTypeList")
    public  HttpResourceResponse<List<VarietyByVarietyTypeVO>> getVarietyByVarietyTypeList(){
        return HttpResourceResponse.success(varietyService.getVarietyByVarietyTypeList());
    }

    /**
     * 获取交易员列表
     * @return 交易员列表
     */
    @ApiOperation(value = "获取交易员列表")
    @PostMapping("/getVarietyByTraderIdList")
    public  HttpResourceResponse<List<VarietyByTraderIdVO>> getVarietyByTraderIdList(){
        return HttpResourceResponse.success(varietyService.getVarietyByTraderIdList());
    }
}
