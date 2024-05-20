package org.orient.otc.dm.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.dm.dto.variety.VarietyTypeAddDto;
import org.orient.otc.dm.dto.variety.VarietyTypeDeleteDto;
import org.orient.otc.dm.dto.variety.VarietyTypeEditDto;
import org.orient.otc.dm.entity.VarietyType;
import org.orient.otc.dm.service.VarietyTypeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 品种信息(VarietyType)表控制层
 *
 * @author makejava
 * @since 2023-07-14 11:18:45
 */
@RestController
@Validated
@RequestMapping("varietyType")
@Api(value = "产业链")
public class VarietyTypeController  {
    /**
     * 服务对象
     */
    @Resource
    private VarietyTypeService varietyTypeService;

    @ApiOperation("获取产业链列表")
    @PostMapping("/list")
    @CheckPermission("dm::varietyType::list")
    public HttpResourceResponse<List<VarietyType>> getList(){
        LambdaQueryWrapper<VarietyType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VarietyType::getIsDeleted, IsDeletedEnum.NO);
        return HttpResourceResponse.success(varietyTypeService.list(queryWrapper));
    }
    @ApiOperation("新增产业链")
    @PostMapping("/add")
    @CheckPermission("dm::varietyType::addVariety")
    public HttpResourceResponse<String> addVariety(@RequestBody @Valid VarietyTypeAddDto varietyTypeAddDto){
        return HttpResourceResponse.success(varietyTypeService.addVarietyType(varietyTypeAddDto));
    }

    @ApiOperation("编辑产业链")
    @PostMapping("/edit")
    @CheckPermission("dm::varietyType::editVariety")
    public HttpResourceResponse<String> editVariety(@RequestBody @Valid VarietyTypeEditDto editDto){
        return HttpResourceResponse.success(varietyTypeService.editVarietyType(editDto));
    }

    @ApiOperation("删除产业链")
    @PostMapping("/delete")
    @CheckPermission("dm::varietyType::delete")
    public HttpResourceResponse<String> deleteVariety(@RequestBody @Valid VarietyTypeDeleteDto deleteDto){
        return HttpResourceResponse.success(varietyTypeService.deleteVarietyType(deleteDto));
    }
}

