package org.orient.otc.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.system.dto.IntegerIdDTO;
import org.orient.otc.system.dto.structure.StructureAddDTO;
import org.orient.otc.system.dto.structure.StructureEditDTO;
import org.orient.otc.system.service.StructureService;
import org.orient.otc.api.system.vo.StructureInfoVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义结构接口
 */
@Api("自定义结构")
@RestController
@RequestMapping("/structureInfo")
public class StructureInfoController {

    @Resource
    private StructureService structureService;

    /**
     * 新增自定义结构
     * @param addDTO 自定义结构内容
     * @return 是否成功
     */
    @ApiOperation(value = "新增自定义结构")
    @PostMapping("/addStructure")
    public HttpResourceResponse<String> addStructure(@RequestBody StructureAddDTO addDTO){
        return HttpResourceResponse.success(structureService.addStructure(addDTO)>0?"保存成功":"保存失败");
    }


    /**
     * 编辑自定义结构
     * @param editDTO 自定义结构内容
     * @return 是否成功
     */
    @ApiOperation(value = "编辑自定义结构")
    @PostMapping("/editStructure")
    public HttpResourceResponse<String> editStructure(@RequestBody StructureEditDTO editDTO){
        return HttpResourceResponse.success(structureService.editStructure(editDTO)>0?"保存成功":"保存失败");
    }


    /**
     * 删除自定义结构
     * @param delDTO 结构ID
     * @return 是否成功
     */
    @ApiOperation(value = "删除自定义结构")
    @PostMapping("/delStructure")
    public HttpResourceResponse<String> delStructure(@RequestBody IntegerIdDTO delDTO){
        return HttpResourceResponse.success(structureService.delStructure(delDTO.getId())>0?"保存成功":"保存失败");
    }


    /**
     * 获取自定义结构列表
     * @return 自定义结构
     */
    @ApiOperation(value = "获取自定义结构列表")
    @GetMapping("/getStructureInfoList")
    public HttpResourceResponse<List<StructureInfoVO>> getStructureInfoList(){
        return HttpResourceResponse.success(structureService.getStructureInfoList());
    }
}
