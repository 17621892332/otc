package org.orient.otc.quote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.trade.ExchangePositionCheckPageListDto;
import org.orient.otc.quote.dto.trade.ExchangeTradePageListDto;
import org.orient.otc.quote.service.ExchangePositionCheckService;
import org.orient.otc.quote.service.ExchangeTradeService;
import org.orient.otc.quote.vo.ExchangePositionCheckVo;
import org.orient.otc.quote.vo.ExchangeTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/trade/exchangePositionCheck")
@Api(tags = "场内持仓校对记录", description = "场内持仓校对记录")
@Slf4j
public class ExchangePositionCheckController {
    @Autowired
    ExchangePositionCheckService exchangePositionCheckService;

    @PostMapping("/selectListByPage")
    @ApiOperation("成交记录分页查询")
    public HttpResourceResponse<IPage<ExchangePositionCheckVo>> selectListByPage(@RequestBody ExchangePositionCheckPageListDto dto){
        return HttpResourceResponse.success(exchangePositionCheckService.selectOptionListByPage(dto));
    }


}
