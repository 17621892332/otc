package org.orient.otc.quote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.trade.ExchangeTradePageListDto;
import org.orient.otc.quote.service.ExchangeTradeService;
import org.orient.otc.quote.vo.ExchangeTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/trade/exchangeTrade")
@Api(tags = "交易流水", description = "交易流水")
@Slf4j
public class ExchangeTradeController {
    @Autowired
    ExchangeTradeService exchangeTradeService;

    @PostMapping("/selectListByPage")
    @ApiOperation("成交记录分页查询")
    public HttpResourceResponse<IPage<ExchangeTradeVo>> selectListByPage(@RequestBody ExchangeTradePageListDto dto){
        return HttpResourceResponse.success(exchangeTradeService.selectOptionListByPage(dto));
    }

    @PostMapping("/tradeExport")
    @ApiOperation("成交记录导出")
    public void tradeExport(@RequestBody ExchangeTradePageListDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        exchangeTradeService.tradeExport(dto,request,response);
    }


}
