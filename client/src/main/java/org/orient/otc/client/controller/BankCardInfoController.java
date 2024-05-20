package org.orient.otc.client.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.client.dto.BankCardInfoAddDto;
import org.orient.otc.client.dto.BankCardInfoDeleteDto;
import org.orient.otc.client.dto.BankCardInfoQueryByClientIdDto;
import org.orient.otc.client.dto.BankCardInfoUpdateDto;
import org.orient.otc.client.service.BankCardInfoService;
import org.orient.otc.client.vo.BankCardInfoVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bankCardInfo")
@Api(tags = "客户银行信息", description = "客户银行信息")
public class BankCardInfoController {
    @Autowired
    BankCardInfoService bankCardInfoService;

    @ApiOperation("根据客户id查询银行信息")
    @PostMapping("/getByClientId")
    public HttpResourceResponse<List<BankCardInfoVO>> getByClientId(@RequestBody @Valid BankCardInfoQueryByClientIdDto dto){
        return HttpResourceResponse.success(bankCardInfoService.getByClientId(dto));
    }

    @ApiOperation("新增客户银行信息")
    @PostMapping("/add")
    public HttpResourceResponse<String> add(@RequestBody @Valid BankCardInfoAddDto dto){
        return HttpResourceResponse.success(bankCardInfoService.add(dto));
    }
    @ApiOperation("修改客户银行信息")
    @PostMapping("/update")
    public HttpResourceResponse<String> update(@RequestBody @Valid BankCardInfoUpdateDto dto){
        return HttpResourceResponse.success(bankCardInfoService.update(dto));
    }

    @ApiOperation("删除客户银行信息")
    @PostMapping("/delete")
    public HttpResourceResponse<String> delete(@RequestBody @Valid BankCardInfoDeleteDto dto){
        return HttpResourceResponse.success(bankCardInfoService.delete(dto));
    }

    @ApiOperation("设置有效或无效")
    @PostMapping("/enable")
    public HttpResourceResponse<String> enable(@RequestBody @Valid BankCardInfoUpdateDto dto){
        return HttpResourceResponse.success(bankCardInfoService.enable(dto));
    }
}
