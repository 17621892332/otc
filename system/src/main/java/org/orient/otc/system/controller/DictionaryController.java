package org.orient.otc.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.system.dto.*;
import org.orient.otc.system.dto.dictionary.*;
import org.orient.otc.system.entity.Dictionary;
import org.orient.otc.system.service.DictionaryService;
import org.orient.otc.system.vo.DictionaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dictionary")
@Api(tags = "字典")
public class DictionaryController {
    @Autowired
    DictionaryService dictionaryService;

    /**
     * 获取字典列表
     * @param dictionaryQueryDto
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation("获取字典列表")
    @CheckPermission("system::dictionary::getList")
    public HttpResourceResponse<List<Dictionary>> getList(@RequestBody DictionaryQueryDto dictionaryQueryDto){
        return HttpResourceResponse.success(dictionaryService.getList(dictionaryQueryDto.getDicTypeCode()));
    }
    @PostMapping("/selectByPage")
    @ApiOperation("分页查询字典列表")
    public HttpResourceResponse<IPage<Dictionary>> selectByPage(@RequestBody @Valid  DictionaryPageDto dto){
        return HttpResourceResponse.success(dictionaryService.selectByPage(dto));
    }

    @PostMapping("/getById")
    @ApiOperation("根据ID获取字典")
    public HttpResourceResponse<Dictionary> getById(@RequestBody @Valid DictionaryGetByIdDto dto){
        return HttpResourceResponse.success(dictionaryService.getById(dto.getDicId()));
    }

    @PostMapping("/add")
    @ApiOperation("新增字典")
    public HttpResourceResponse<String> add(@RequestBody @Valid DictionaryAddDto dto){
        return HttpResourceResponse.success(dictionaryService.add(dto));
    }

    @PostMapping("/update")
    @ApiOperation("修改字典")
    public HttpResourceResponse<String> update(@RequestBody @Valid DictionaryUpdateDto dto){
        return HttpResourceResponse.success(dictionaryService.updateDictionary(dto));
    }

    @PostMapping("/delete")
    @ApiOperation("删除字典列表")
    public HttpResourceResponse<String> delete(@RequestBody @Valid DictionaryDeleteDto dto){
        return HttpResourceResponse.success(dictionaryService.deleteDictionary(dto));
    }

    @PostMapping("/updateSort")
    @ApiOperation("修改字典排序")
    public HttpResourceResponse<String> updateSort(@RequestBody @Valid DictionarySortDto dto){
        return HttpResourceResponse.success(dictionaryService.updateSort(dto));
    }
    /**
     * 获取字典列表（分组/排序）
     */
    @PostMapping("/getListAll")
    @ApiOperation("获取字典列表")
    public HttpResourceResponse<Map<String, List<DictionaryVo>>> getListAll(){
        return HttpResourceResponse.success(dictionaryService.getListAll());
    }

    /**
     * 获取字典列表
     * @param dictionaryQueryDto
     * @return
     */
    @PostMapping("/getListByDictTypeCode")
    @ApiOperation("获取字典列表")
    public HttpResourceResponse<List<DictionaryVo>> getListByDictTypeCode(@RequestBody @Valid DictionaryQueryDto dictionaryQueryDto){
        return HttpResourceResponse.success(dictionaryService.getListByDictTypeCode(dictionaryQueryDto.getDicTypeCode()));
    }
}
