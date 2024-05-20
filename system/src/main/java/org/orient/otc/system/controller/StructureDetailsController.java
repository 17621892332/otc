package org.orient.otc.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.system.dto.IntegerIdDTO;
import org.orient.otc.system.dto.structure.StructureDetailsAddDTO;
import org.orient.otc.system.dto.structure.StructureDetailsEditDTO;
import org.orient.otc.system.service.StructureDetailsService;
import org.orient.otc.system.vo.structure.StructureDetailsVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义结构详情接口
 */
@Api("自定义结构详情")
@RestController
@RequestMapping("/structureDetails")
public class StructureDetailsController {

    @Resource
    private StructureDetailsService structureDetailsService;

    /**
     * 新增自定义结构详情
     * @param addDTO 自定义结构详情内容
     * @return 是否成功
     */
    @ApiOperation(value = "新增自定义结构详情")
    @PostMapping("/addStructureDetails")
    public HttpResourceResponse<String> addStructureDetails(@RequestBody StructureDetailsAddDTO addDTO){
        return HttpResourceResponse.success(structureDetailsService.addStructureDetails(addDTO)>0?"保存成功":"保存失败");
    }


    /**
     * 编辑自定义结构详情
     * @param editDTO 自定义结构详情内容
     * @return 是否成功
     */
    @ApiOperation(value = "编辑自定义结构详情")
    @PostMapping("/editStructureDetails")
    public HttpResourceResponse<String> editStructureDetails(@RequestBody StructureDetailsEditDTO editDTO){
        return HttpResourceResponse.success(structureDetailsService.editStructureDetails(editDTO)>0?"保存成功":"保存失败");
    }


    /**
     * 删除自定义结构详情
     * @param delDTO 结构详情ID
     * @return 是否成功
     */
    @ApiOperation(value = "删除自定义结构详情")
    @PostMapping("/delStructureDetails")
    public HttpResourceResponse<String> delStructureDetails(@RequestBody IntegerIdDTO delDTO){
        return HttpResourceResponse.success(structureDetailsService.delStructureDetails(delDTO.getId())>0?"保存成功":"保存失败");
    }


    /**
     * 获取自定义结构详情列表
     * @param structureId 结构ID
     * @return 自定义结构详情
     */
    @ApiOperation(value = "获取自定义结构详情列表")
    @GetMapping("/getStructureDetailsList")
    public HttpResourceResponse<List<StructureDetailsVO>> getStructureDetailsList(@RequestParam Integer structureId){
        return HttpResourceResponse.success(structureDetailsService.getStructureDetailsList(structureId));
    }
}
