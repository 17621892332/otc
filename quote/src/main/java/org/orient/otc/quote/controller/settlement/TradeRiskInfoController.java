package org.orient.otc.quote.controller.settlement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.quote.vo.TradeRiskPVInfoVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.TradeRiskInfoPvDTO;
import org.orient.otc.quote.dto.risk.ReSetRiskPnlDTO;
import org.orient.otc.quote.service.TradeRiskInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 日终风险信息
 */
@RestController
@RequestMapping("/tradeRiskInfo")
@Api(tags = "日终风险信息", consumes = "风险")
@Validated
public class TradeRiskInfoController {

    @Resource
    private TradeRiskInfoService tradeRiskInfoService;

    /**
     * 重算风险盈亏数据
     * @param reSetRiskPnlDTO 开始日期
     * @return 是否成功
     */
    @PostMapping("reSetRiskPnl")
    public HttpResourceResponse<Boolean> reSetRiskPnl(@RequestBody ReSetRiskPnlDTO reSetRiskPnlDTO) {
        return HttpResourceResponse.success(tradeRiskInfoService.reSetTodayPnl(reSetRiskPnlDTO.getRiskDate(), reSetRiskPnlDTO.getIsHavingNext()));
    }
    /**
     * 初始化TotalPnl
     * @param reSetRiskPnlDTO 开始日期
     * @return 是否成功
     */
    @PostMapping("initTotalPnl")
    public HttpResourceResponse<Boolean> initTotalPnl(@RequestBody ReSetRiskPnlDTO reSetRiskPnlDTO) {
        return HttpResourceResponse.success(tradeRiskInfoService.initTotalPnl(reSetRiskPnlDTO.getRiskDate()));
    }

    /**
     *  导入风险信息
     * @param file 风险信息
     * @return 导入信息
     * @throws Exception 文件异常
     */
    @ApiOperation("导入自定义风险信息")
    @PostMapping("importRiskInfo")
    public HttpResourceResponse<String> importRiskInfo(@RequestParam("file") MultipartFile file) throws Exception {
        return HttpResourceResponse.success(tradeRiskInfoService.importRiskInfo(file));
    }

    /**
     * 获取风险维护数据
     * @param tradeRiskInfoPvDTO 查询条件
     * @return 风险分页信息
     */
    @ApiOperation("获取风险维护数据")
    @PostMapping("/getTradeRiskPVInfoByPage")
    public HttpResourceResponse<IPage<TradeRiskPVInfoVO>> getTradeRiskPvInfoByPage(@RequestBody TradeRiskInfoPvDTO tradeRiskInfoPvDTO){
        return HttpResourceResponse.success(tradeRiskInfoService.getTradeRiskPvByPage(tradeRiskInfoPvDTO));
    }

}
