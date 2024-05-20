package org.orient.otc.quote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.TradeRiskInfoDto;
import org.orient.otc.quote.dto.risk.InitPosDataDto;
import org.orient.otc.quote.dto.risk.PositionPageListDto;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.service.RiskService;
import org.orient.otc.quote.service.SettlementService;
import org.orient.otc.quote.util.HutoolUtil;
import org.orient.otc.quote.vo.PositionPageListVo;
import org.orient.otc.quote.vo.TradeRiskInfoExportVo;
import org.orient.otc.quote.vo.TradeRiskInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/trade/risk")
@Api(tags = "风险", description = "风险")
@Slf4j
public class RiskController {
    @Autowired
    RiskService riskService;
    @Autowired
    SettlementService settlementService;

    @Autowired
    CalendarClient calendarClient;

    @PostMapping("/reCalculationPos")
    @ApiOperation("重新计算持仓")
    @CheckPermission("quote::trade/risk::reCalculationPos")
    public HttpResourceResponse<Boolean> reCalculationPos() {
        return HttpResourceResponse.success(riskService.reCalculationPos());
    }

    @PostMapping("/getExchangeTrade")
    @ApiOperation("重新获取场内交易记录")
    @CheckPermission("quote::trade/risk::getExchangeTrade")
    public HttpResourceResponse<Boolean> getExchangeTrade() {
        return HttpResourceResponse.success(settlementService.getExchangeTrade());
    }

    @PostMapping("/getExchangePosition")
    @ApiOperation("重新获取场内持仓")
    @CheckPermission("quote::trade/risk::getExchangePosition")
    public HttpResourceResponse<Boolean> afreshGetExchangePosition() {
        return HttpResourceResponse.success(settlementService.getExchangePosition());
    }


    @PostMapping("/initPosData")
    @ApiOperation("初始化持仓数据")
    @CheckPermission("quote::trade/risk::initPosData")
    public HttpResourceResponse<Boolean> initPosData(@RequestBody InitPosDataDto initPosDataDto) {
        return HttpResourceResponse.success(riskService.initPosData(initPosDataDto.getLastDate()));
    }

    @PostMapping("/checkTodayCaclPos")
    @ApiOperation("校验今日持仓")
    @CheckPermission("quote::trade/risk::checkTodayCaclPos")
    public HttpResourceResponse<Boolean> checkTodayCaclPos() {
        return HttpResourceResponse.success(settlementService.checkTodayCaclPos());
    }
    @GetMapping("/updatePosData")
    @ApiOperation("更新场内持仓信息")
    @CheckPermission("quote::trade/risk::updatePosData")
    public HttpResourceResponse<SettlementVO> updatePosData(@RequestParam String today) {
        return HttpResourceResponse.success(riskService.updatePosData(today));
    }


    @PostMapping("/fromTmpToExchangePosition")
    @ApiOperation("将临时表的持仓加载到正式表中")
    @CheckPermission("quote::trade/risk::fromTmpToExchangePosition")
    public HttpResourceResponse<Boolean> fromTmpToExchangePosition() {
        return HttpResourceResponse.success(riskService.fromTmpToExchangePosition());
    }
    @PostMapping("/fromTmpToExchangeTrade")
    @ApiOperation("将临时表的交易记录加载到正式表中")
    @CheckPermission("quote::trade/risk::fromTmpToExchangeTrade")
    public HttpResourceResponse<Boolean> fromTmpToExchangeTrade() {
        return HttpResourceResponse.success(riskService.fromTmpToExchangeTrade());
    }

    @PostMapping("/selectListByPage")
    @ApiOperation("日终持仓风险分页查询")
    @CheckPermission("quote::trade/risk::selectListByPage")
    public HttpResourceResponse<IPage<TradeRiskInfoVo>> selectListByPage(@RequestBody TradeRiskInfoDto dto){
        return HttpResourceResponse.success(riskService.selectListByPage(dto));
    }

    @PostMapping("/exportRisk")
    @ApiOperation("日终持仓风险查询导出")
    @CheckPermission("quote::trade/risk::exportRisk")
    public void exportRisk(@RequestBody TradeRiskInfoDto dto,HttpServletRequest request, HttpServletResponse response){
        List<TradeRiskInfoExportVo> list = riskService.getExportData(dto);
        if(null == list || list.isEmpty()){
            BussinessException.E_300101.assertTrue(Boolean.FALSE);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            // 文件名称 = 风险导出+时间戳
            String fileName = "风险导出"+sdf.format(calendar.getTime())+calendar.get(Calendar.MILLISECOND);
            HutoolUtil.export(list,fileName,"风险导出",TradeRiskInfoExportVo.class,request,response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/resetOpenAndeClose")
    @ApiOperation("重置今日开平仓数据")
    public HttpResourceResponse<String> resetOpenAndeClose(){
        return HttpResourceResponse.success(riskService.setTodayOpenAndClose());
    }

    @PostMapping("/selectPosListByPage")
    @ApiOperation("持仓记录分页查询")
    @CheckPermission("quote::trade/risk::selectPosListByPage")
    public HttpResourceResponse<IPage<PositionPageListVo>> selectPosListByPage(@RequestBody PositionPageListDto dto) throws Exception {
        return HttpResourceResponse.success(riskService.selectPosListByPage(dto));
    }
    @PostMapping("/exportPos")
    @ApiOperation("持仓记录导出")
    @CheckPermission("quote::trade/risk::exportPos")
    public void exportPos(@RequestBody PositionPageListDto dto,HttpServletRequest request, HttpServletResponse response) throws Exception {
        riskService.exportPos(dto,request,response);
    }

    @PostMapping("/exportDefinitionRisk")
    @ApiOperation("自定义风险查询导出")
    @CheckPermission("quote::trade/risk::exportDefinitionRisk")
    public void exportDefinitionRisk(@RequestBody TradeRiskInfoDto dto,HttpServletRequest request, HttpServletResponse response){
        if (dto.getSettlementDate()==null) {
            BussinessException.E_300101.assertTrue(false,"结算日期必填");
        }
        riskService.getExportDefinitionRisk(dto,request,response);
    }

}
