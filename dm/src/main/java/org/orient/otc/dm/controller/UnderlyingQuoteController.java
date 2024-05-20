package org.orient.otc.dm.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.dm.vo.UnderlyingQuoteVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.dm.dto.underlyingQuote.UnderlyingQuoteUpdateDTO;
import org.orient.otc.dm.service.UnderlyinQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/underlyingQuote")
@Api(tags = "报价合约管理")
public class UnderlyingQuoteController {
    @Autowired
    private UnderlyinQuoteService underlyinQuoteService;

    /**
     * 获取所有报价合约列表
     * @return 报价合约列表
     */
    @ApiOperation("报价合约列表")
    @PostMapping("/list")
    public HttpResourceResponse<List<UnderlyingQuoteVO>> getList(){
        return HttpResourceResponse.success(underlyinQuoteService.getList());
    }

    @ApiOperation("修改报价合约")
    @PostMapping("/update")

    public HttpResourceResponse<String> update(@RequestBody @Valid List<UnderlyingQuoteUpdateDTO> dtoList){
        return HttpResourceResponse.success(underlyinQuoteService.update(dtoList));
    }


}
