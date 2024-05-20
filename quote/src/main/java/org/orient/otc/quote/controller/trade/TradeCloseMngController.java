package org.orient.otc.quote.controller.trade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.trade.RollbackTradeCloseMngDTO;
import org.orient.otc.quote.dto.trade.TradeCloseInsertDTO;
import org.orient.otc.quote.dto.trade.TradeCloseQueryDto;
import org.orient.otc.quote.entity.TradeCloseMng;
import org.orient.otc.quote.service.TradeCloseMngService;
import org.orient.otc.quote.vo.trade.TradeCloseMngVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 平仓模块
 */
@RestController
@RequestMapping("/trade/close")
@Api(tags = "平仓")
public class TradeCloseMngController {
    @Autowired
    TradeCloseMngService tradeCloseMngService;

    /**
     * 录入平仓
     * @param tradeCloseInsertDto 平仓信息
     * @return 录入后信息
     */
    @PostMapping("/insert")
    @ApiOperation("录入平仓")
    @CheckPermission("quote::trade/close::insert")
    public HttpResourceResponse<List<TradeCloseMng>> insertTradeClose(@RequestBody @Valid TradeCloseInsertDTO tradeCloseInsertDto) {
        return HttpResourceResponse.success(tradeCloseMngService.insertTradeClose(tradeCloseInsertDto));
    }

    /**
     * 通过组合代码获取平仓明细
     * @param tradeCloseQueryDto 组合代码
     * @return 平仓信息
     */
    @PostMapping("/getTradeCloseMngInfoByCombCode")
    @ApiOperation("通过组合代码获取平仓明细")
    @CheckPermission("quote::trade/close::getTradeCloseMngInfoByCombCode")
    public HttpResourceResponse<List<TradeCloseMngVO>> getTradeCloseMngInfoByCombCode(@RequestBody @Valid TradeCloseQueryDto tradeCloseQueryDto) {
        return HttpResourceResponse.success(tradeCloseMngService.getTradeCloseMngInfoByCombCode(tradeCloseQueryDto));
    }

    /**
     * 平仓回退
     * @param rollbackTradeCloseMngDto 平仓ID
     * @return 回退信息
     */
    @PostMapping("/rollbackTradeCloseMng")
    @ApiOperation("交易回退")
    @CheckPermission("quote::trade/close::rollbackTradeCloseMng")
    public HttpResourceResponse<String> rollbackTradeCloseMng(@RequestBody @Valid RollbackTradeCloseMngDTO rollbackTradeCloseMngDto){
        return HttpResourceResponse.success(tradeCloseMngService.rollbackTradeCloseMng(rollbackTradeCloseMngDto));
    }

}
