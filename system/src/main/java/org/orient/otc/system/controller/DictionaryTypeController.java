package org.orient.otc.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.system.dto.dictionarytype.*;
import org.orient.otc.system.entity.DictionaryType;
import org.orient.otc.system.service.DictionaryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 字典类型
 */
@RestController
@RequestMapping("/dictionaryType")
@Api(tags = "字典类型")
public class DictionaryTypeController {
    @Autowired
    DictionaryTypeService dictionaryTypeService;

    /**
     * 获取字典类型列表
     * @return 字典类型
     */
    @PostMapping("/getDictionaryTypeList")
    @ApiOperation("获取字典类型列表")
    @CheckPermission("system::dictionary::getDictionaryTypeList")
    public HttpResourceResponse<List<DictionaryType>> getDictionaryTypeList() {
        return HttpResourceResponse.success(dictionaryTypeService.getDictionaryTypeList());
    }

    @PostMapping("/getById")
    @ApiOperation("根据ID获取字典类型")
    public HttpResourceResponse<DictionaryType> getById(@RequestBody @Valid  DictionaryTypeGetByIdDto dto) {
        return HttpResourceResponse.success(dictionaryTypeService.getById(dto.getDicTypeId()));
    }

    @PostMapping("/add")
    @ApiOperation("新增字典类型")
    public HttpResourceResponse<String> add(@RequestBody @Valid DictionaryTypeAddDto dto) {
        return HttpResourceResponse.success(dictionaryTypeService.add(dto));
    }

    @PostMapping("/update")
    @ApiOperation("修改字典类型")
    public HttpResourceResponse<String> update(@RequestBody @Valid DictionaryTypeUpdateDto dto) {
        return HttpResourceResponse.success(dictionaryTypeService.update(dto));
    }

    @PostMapping("/delete")
    @ApiOperation("删除字典类型")
    public HttpResourceResponse<String> delete(@RequestBody @Valid DictionaryTypeDeleteDto dto) {
        return HttpResourceResponse.success(dictionaryTypeService.delete(dto));
    }
    @PostMapping("/updateSort")
    @ApiOperation("修改字典类型排序")
    public HttpResourceResponse<String> updateSort(@RequestBody @Valid DictionaryTypeSortDto dto) {
        return HttpResourceResponse.success(dictionaryTypeService.updateSort(dto));
    }
    @PostMapping("/selectByPage")
    @ApiOperation("分页查询字典类型列表")
    public HttpResourceResponse<IPage<DictionaryType>> selectByPage(@RequestBody DictionaryTypePageDto dto) {
        return HttpResourceResponse.success(dictionaryTypeService.selectByPage(dto));
    }


}
