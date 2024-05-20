package org.orient.otc.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.ExchangeAccount;
import org.orient.otc.user.service.ExchangeAccountService;
import org.orient.otc.user.vo.ExchangeAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 对冲账户API
 */
@RestController
@RequestMapping("/exchangeAccount")
@Api(tags = "对冲账户 API")
public class ExchangeAccountrController {

    @Autowired
    ExchangeAccountService exchangeAccountService;

    /**
     * 对冲账户分页查询
     * @param dto 查询条件
     * @return 对冲账户分页列表
     */
    @ApiOperation("对冲账户分页查询")
    @PostMapping("/getListByPage")
    public HttpResourceResponse<IPage<ExchangeAccountVO>> getListByPage(@RequestBody ExchangeAccountPageListDto dto){
        return HttpResourceResponse.success(exchangeAccountService.getListByPage(dto));
    }

    @ApiOperation("对冲账户详情查询")
    @PostMapping("/getExchangeAccountDetail")
    public HttpResourceResponse<ExchangeAccount> getExchangeAccountDetail(@RequestBody @Valid ExchangeAccountDetailDto dto){
        return HttpResourceResponse.success(exchangeAccountService.getExchangeAccountDetail(dto));
    }

    @ApiOperation("新增对冲账户")
    @PostMapping("/addExchangeAccount")
    public HttpResourceResponse<String> addExchangeAccount(@RequestBody ExchangeAccountAddDto dto){
        return HttpResourceResponse.success(exchangeAccountService.addExchangeAccount(dto));
    }

    @ApiOperation("修改对冲账户")
    @PostMapping("/updateExchangeAccount")
    public HttpResourceResponse<String> updateExchangeAccount(@RequestBody ExchangeAccountUpdateDto dto){
        return HttpResourceResponse.success(exchangeAccountService.updateExchangeAccount(dto));
    }

    @ApiOperation("删除对冲账户")
    @PostMapping("/deleteExchangeAccount")
    public HttpResourceResponse<String> deleteExchangeAccount(@RequestBody ExchangeAccountDeleteDto dto){
        return HttpResourceResponse.success(exchangeAccountService.deleteExchangeAccount(dto));
    }
    @ApiOperation("禁用/启用")
    @PostMapping("/updateStatus")
    public HttpResourceResponse<String> updateStatus(@RequestBody @Valid ExchangeAccountUpdateStatusDto dto){
        return HttpResourceResponse.success(exchangeAccountService.updateStatus(dto));
    }

}
