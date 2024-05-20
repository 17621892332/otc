package org.orient.otc.quote.controller.trade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.dm.dto.TradayAddDaysDto;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.file.vo.MinioUploadVO;
import org.orient.otc.api.quote.vo.TradeMngDetailVO;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.confirmbook.DownloadTradeConfirmBookDTO;
import org.orient.otc.quote.dto.settlementbook.BuildSettlementConfirmBookDTO;
import org.orient.otc.quote.dto.settlementbook.DownloadSettlementConfirmBookDTO;
import org.orient.otc.quote.dto.trade.*;
import org.orient.otc.quote.service.TradeMngService;
import org.orient.otc.quote.vo.trade.SettlementConfirmBookVO;
import org.orient.otc.quote.vo.trade.TradeConfirmBookVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易相关接口
 */
@RestController
@RequestMapping("/trade")
@Api(tags = "交易")
@Validated
public class TradeMngController {
    @Resource
    private TradeMngService tradeMngService;
    @Resource
    private CalendarClient calendarClient;


    @PostMapping("/insert")
    @ApiOperation("录入交易")
    @CheckPermission("quote::trade::insert")
    public HttpResourceResponse<List<TradeMngVO>> insertTrade(@RequestBody @Valid TradeInsertDTO tradeInsertDto){
        return HttpResourceResponse.success(tradeMngService.insertTrade(tradeInsertDto));
    }
    @PostMapping("/update")
    @ApiOperation("更新交易")
    @CheckPermission("quote::trade::update")
    public HttpResourceResponse<String> updateTrade(@RequestBody @Valid TradeUpdateDTO tradeUpdateDTO){
        return HttpResourceResponse.success(tradeMngService.updateTrade(tradeUpdateDTO));
    }
    @PostMapping("/delete")
    @ApiOperation("删除交易")
    @CheckPermission("quote::trade::delete")
    public HttpResourceResponse<String> delete(@RequestBody @Valid CombCodeDTO combCodeDTO){
        return HttpResourceResponse.success(tradeMngService.delete(combCodeDTO));
    }

    @PostMapping("/getTradeInfo")
    @ApiOperation("查看交易详情")
    @CheckPermission("quote::trade::getTradeInfo")
    public HttpResourceResponse<List<TradeMngVO>> getTradeInfo(@RequestBody @Valid CombCodeDTO combCodeDTO){
        return HttpResourceResponse.success(tradeMngService.getTradeInfo(combCodeDTO));
    }
    @PostMapping("/getDetailByTradeCode")
    @ApiOperation("查看交易详情根据交易编号")
    @CheckPermission("quote::trade::getTradeInfo")
    public HttpResourceResponse<TradeMngDetailVO> getByTradeCode(@RequestBody @Valid TradeDetailDto dto){
        return HttpResourceResponse.success(tradeMngService.getByTradeCode(dto));
    }

    @PostMapping("/queryTradeListByTradeCodeList")
    @ApiOperation("通过交易编号列表获取交易列表")
    @CheckPermission("quote::trade::queryTradeListByTradeCodeList")
    public HttpResourceResponse<List<TradeMngVO>> queryTradeListByTradeCodeList(@RequestBody @Valid TradeCodeQueryDTO dto){
        return HttpResourceResponse.success(tradeMngService.queryTradeListByTradeCodeList(dto));
    }
    @PostMapping("queryTradeList")
    @ApiOperation("查询交易列表")
    @CheckPermission("quote::trade::queryTradeList")
    public  HttpResourceResponse<IPage<TradeMngVO>> queryTradeList(@RequestBody @Valid TradeQueryDTO tradeQueryDto){
        return HttpResourceResponse.success(tradeMngService.queryTradeList(tradeQueryDto));
    }

    @PostMapping("getTodayDay1pnlTotal")
    @ApiOperation("获取pnlday1Total")
    @CheckPermission("quote::trade::getTodayDay1pnlTotal")
    public HttpResourceResponse<LocalDate> getTodayDay1pnlTotal(@RequestParam String tradeCode){
        TradayAddDaysDto tradayAddDaysDto = new TradayAddDaysDto();
        tradayAddDaysDto.setDate(LocalDate.now());
        tradayAddDaysDto.setDays(1);
        return HttpResourceResponse.success(calendarClient.tradeDayAddDays(tradayAddDaysDto));
    }

    @PostMapping("getListBypage")
    @ApiOperation("分页查询交易列表")
    @CheckPermission("quote::trade::getListBypage")
    public  HttpResourceResponse<IPage<TradeMngVO>> getListBypage(@RequestBody @Valid TradeQueryPageDto dto){
        return HttpResourceResponse.success(tradeMngService.getListBypage(dto));
    }
    @PostMapping("tradeConfirmBookSelectByPage")
    @ApiOperation("交易确认书分页查询")
    @CheckPermission("quote::trade::tradeConfirmBookSelectByPage")
    public  HttpResourceResponse<IPage<TradeConfirmBookVO>> tradeConfirmBookSelectByPage(@RequestBody @Valid TradeConfirmBookQueryDTO queryDTO){
        return HttpResourceResponse.success(tradeMngService.tradeConfirmBookSelectByPage(queryDTO));
    }
    @PostMapping("buildTradeConfirmBook")
    @ApiOperation("生成交易确认书")
    @CheckPermission("quote::trade::buildTradeConfirmBook")
    public HttpResourceResponse<List<MinioUploadVO>> buildTradeConfirmBook(@RequestBody @Valid BuildTradeConfirmBookDto dto) throws Exception {
        return HttpResourceResponse.success(tradeMngService.buildTradeConfirmBook(dto));
    }
    @PostMapping("batchDownloadTradeConfirmBook")
    @ApiOperation("批量下载交易确认书")
    @CheckPermission("quote::trade::export")
    public  void batchDownloadTradeConfirmBook(@RequestBody @Valid DownloadTradeConfirmBookDTO dto, HttpServletResponse response) {
        tradeMngService.batchDownloadTradeConfirmBook(dto,response);
    }
    @PostMapping("settlementConfirmBookSelectByPage")
    @ApiOperation("结算确认书分页查询")
    @CheckPermission("quote::trade::settlementConfirmBookSelectByPage")
    public  HttpResourceResponse<IPage<SettlementConfirmBookVO>> settlementConfirmBookSelectByPage(@RequestBody @Valid TradeSettlementConfirmBookQueryDTO dto){
        return HttpResourceResponse.success(tradeMngService.settlementConfirmBookSelectByPage(dto));
    }
    @PostMapping("buildSettlementConfirmBook")
    @ApiOperation("生成结算确认书")
    @CheckPermission("quote::trade::buildSettlementConfirmBook")
    public HttpResourceResponse<List<MinioUploadVO>>  buildSettlementConfirmBook(@RequestBody @Valid BuildSettlementConfirmBookDTO dto) throws Exception {
       return HttpResourceResponse.success(tradeMngService.buildSettlementConfirmBook(dto));
    }
    @PostMapping("batchDownloadSettlementConfirmBook")
    @ApiOperation("批量下载结算确认书")
    @CheckPermission("quote::trade::export")
    public  void batchDownloadSettlementConfirmBook(@RequestBody @Valid DownloadSettlementConfirmBookDTO dto, HttpServletResponse response) {
        tradeMngService.batchDownloadSettlementConfirmBook(dto,response);
    }
    @PostMapping("export")
    @ApiOperation("导出交易")
    @CheckPermission("quote::trade::export")
    public  void export(@RequestBody @Valid TradeQueryPageDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        tradeMngService.export(dto,request,response);
    }

    /**
     * 更新结算方式
     * @param updateTradeSettleTypeDTO 更新参数
     * @return 更新结果
     */
    @PostMapping("updateTradeSettleType")
    @ApiOperation("更新结算方式")
    @CheckPermission("quote::trade::updateTradeSettleType")
    public HttpResourceResponse<String> updateTradeSettleType(@RequestBody UpdateTradeSettleTypeDTO updateTradeSettleTypeDTO){
        return HttpResourceResponse.success(tradeMngService.updateTradeSettleType(updateTradeSettleTypeDTO));
    }
}
