package org.orient.otc.quote.controller.trade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.trade.TradeDetailPageListDto;
import org.orient.otc.quote.service.TradeDetailService;
import org.orient.otc.quote.vo.trade.ObsTradeDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/tradeDetail")
@Api(tags = "生成远期管理")
@Validated
public class TradeDetailController {

    @Autowired
    TradeDetailService tradeDetailService;

    @PostMapping("/selectListByPage")
    @ApiOperation("分页查询")
    public HttpResourceResponse<IPage<ObsTradeDetailVo>> selectListByPage(@RequestBody @Valid TradeDetailPageListDto dto){
        return HttpResourceResponse.success(tradeDetailService.selectListByPage(dto));
    }

}
