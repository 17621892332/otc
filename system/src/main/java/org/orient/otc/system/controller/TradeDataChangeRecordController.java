package org.orient.otc.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.quote.feign.TradeMngClient;
import org.orient.otc.api.system.feign.SystemDataChangeRecordClient;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordDetailDto;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordPageDto;
import org.orient.otc.system.service.TradeDataChangeRecordService;
import org.orient.otc.system.vo.TradeDataChangeRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/tradeDataChangeRecord")
@Api(tags = "交易数据变更")
public class TradeDataChangeRecordController {
    @Autowired
    TradeDataChangeRecordService tradeDataChangeRecordService;

    @Resource
    SystemDataChangeRecordClient systemDataChangeRecordClient;
    @Autowired
    TradeMngClient tradeMngClient;
    @Autowired
    ObjectEqualsUtil objectEqualsUtil;

    @PostMapping("/selectByPage")
    @ApiOperation("分页查询")
    public HttpResourceResponse<IPage<TradeDataChangeRecordVO>> selectByPage(@RequestBody TradeDataChangeRecordPageDto dto){
        return HttpResourceResponse.success(tradeDataChangeRecordService.selectByPage(dto));
    }

    @PostMapping("/getDetails")
    @ApiOperation("查看详情")
    public HttpResourceResponse<TradeDataChangeRecordVO> getDetails(@RequestBody TradeDataChangeRecordDetailDto dto){
        return HttpResourceResponse.success(tradeDataChangeRecordService.getDetails(dto));
    }

}
