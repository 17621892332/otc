package org.orient.otc.quote.controller.trade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.trade.TradeMsgQueryDto;
import org.orient.otc.quote.dto.trade.TradeMsgSaveDto;
import org.orient.otc.quote.entity.TradeMsg;
import org.orient.otc.quote.service.TradeMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 交易简讯模块
 * @author dzrh
 */
@RestController
@RequestMapping("/tradeMsg")
@Api(tags = "交易简讯", consumes = "交易")
@Validated
public class TradeMsgController {

    @Autowired
    private TradeMsgService tradeMsgService;

    /**
     * 保存简讯信息
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation("保存简讯信息")
    @CheckPermission("quote::tradeMsg::saveOrUpdate")
    public HttpResourceResponse<Boolean> saveOrUpdate(@RequestBody @Valid TradeMsgSaveDto tradeMsgSaveDto) {
        return HttpResourceResponse.success(tradeMsgService.saveOrUpdateBatchByTradeId(tradeMsgSaveDto.getMsgList()));
    }

    /**
     * 获取简讯信息
     */
    @PostMapping("/getMsgInfo")
    @ApiOperation("获取简讯信息")
    @CheckPermission("quote::tradeMsg::getMsgInfo")
    public HttpResourceResponse<TradeMsg> getMsgInfo(@RequestBody @Valid TradeMsgQueryDto queryDto) {
        return HttpResourceResponse.success(tradeMsgService.queryMsgInfo(queryDto));
    }

}
