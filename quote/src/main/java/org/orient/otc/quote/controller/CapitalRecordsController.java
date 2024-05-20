package org.orient.otc.quote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.capitalrecords.*;
import org.orient.otc.quote.service.CapitalRecordsService;
import org.orient.otc.quote.vo.CapitalRecordsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/capitalRecord")
@Api(tags = "资金记录")
public class CapitalRecordsController {
    @Autowired
    CapitalRecordsService capitalRecordsService;

    @ApiOperation("资金记录分页查询")
    @PostMapping("/getListByPage")
    public HttpResourceResponse<IPage<CapitalRecordsVO>> getListByPage(@RequestBody @Valid CapitalRecordsPageDto dto){
        return HttpResourceResponse.success(capitalRecordsService.getListByPage(dto));
    }

    @ApiOperation("新增-资金记录")
    @PostMapping("/add")
    public HttpResourceResponse<String> add(@RequestBody @Valid CapitalRecordsAddDto dto){
        Integer add = capitalRecordsService.add(dto);
        if (add>0) {
            return HttpResourceResponse.success("操作成功");
        }
        return HttpResourceResponse.success("操作失败");
    }

    @ApiOperation("删除-资金记录")
    @PostMapping("/delete")
    public HttpResourceResponse<String> delete(@RequestBody @Valid CapitalRecordsDeleteDto dto){
        return HttpResourceResponse.success(capitalRecordsService.delete(dto));
    }

    @ApiOperation("更新资金状态")
    @PostMapping("/updateCapitalStatus")
    public HttpResourceResponse<String> updateCapitalStatus(@RequestBody @Valid CapitalRecordsUpdateCapitalStatusDto dto){
        return HttpResourceResponse.success(capitalRecordsService.updateCapitalStatus(dto));
    }

    @ApiOperation("追加备注")
    @PostMapping("/addRemark")
    public HttpResourceResponse<String> addRemark(@RequestBody @Valid CapitalRecordsUpdateRemarkDto dto){
        return HttpResourceResponse.success(capitalRecordsService.addRemark(dto));
    }

    @ApiOperation("导出")
    @PostMapping("/export")
    public void export(@RequestBody CapitalRecordsExportDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        capitalRecordsService.export(dto,request,response);
    }
    @ApiOperation("导入")
    @PostMapping("/import")
    public HttpResourceResponse<String> importCapital(@RequestParam("file")MultipartFile file) throws Exception {
        return HttpResourceResponse.success(capitalRecordsService.importCapital(file));
    }


}
