package org.orient.otc.client.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.client.dto.client.*;
import org.orient.otc.client.service.GrantCreditService;
import org.orient.otc.client.vo.client.GrantCreditVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/grantCredit")
@Api(tags = "授信管理", description = "授信管理接口")
public class GrantCreditController {
    @Autowired
    private GrantCreditService grantCreditService;

    @ApiOperation("获取授信列表分页")
    @PostMapping("getListByPage")
    public HttpResourceResponse<IPage<GrantCreditVO>> getListByPage(@RequestBody  GrantCreditPageDto dto){
        return HttpResourceResponse.success(grantCreditService.getListByPage(dto));
    }

    @ApiOperation("新增授信记录")
    @PostMapping("/add")
    public HttpResourceResponse<String> add(@RequestBody @Valid GrantCreditAddDto dto){
        return grantCreditService.add(dto);
    }

    @ApiOperation("修改授信记录")
    @PostMapping("/update")
    public HttpResourceResponse<String> updateGrantCredit(@RequestBody  @Valid GrantCreditUpdateDto dto){
        return grantCreditService.updateGrantCredit(dto);
    }

    @ApiOperation("删除授信记录")
    @PostMapping("/delete")
    public HttpResourceResponse<String> deleteGrantCredit(@RequestBody  @Valid GrantCreditDeleteDto dto){
        return grantCreditService.deleteGrantCredit(dto);
    }


    @ApiOperation("审批")
    @PostMapping("/check")
    public HttpResourceResponse<String> check(@RequestBody  @Valid GrantCreditCheckDto dto){
        return grantCreditService.check(dto);
    }

    @ApiOperation("授信导入")
    @PostMapping("/import")
    public HttpResourceResponse<String> importGrant(@RequestPart("file") MultipartFile file) throws Exception {
        return HttpResourceResponse.success(grantCreditService.importGrant(file));
    }

}
