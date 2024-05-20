package org.orient.otc.quote.controller.settlement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.risk.CapitalMonitorDTO;
import org.orient.otc.quote.dto.settlementReport.ExportAllAccSummaryDTO;
import org.orient.otc.quote.dto.settlementReport.MailDTO;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.service.CapitalRecordsService;
import org.orient.otc.quote.service.SettlementReportService;
import org.orient.otc.quote.service.TradeCloseMngService;
import org.orient.otc.quote.service.TradeRiskInfoService;
import org.orient.otc.quote.vo.AccSummaryVO;
import org.orient.otc.quote.vo.CapitalRecordsVO;
import org.orient.otc.quote.vo.TradeRiskInfoVo;
import org.orient.otc.quote.vo.settlementreport.AccountOverviewVO;
import org.orient.otc.quote.vo.settlementreport.CapitalMonitorVO;
import org.orient.otc.quote.vo.trade.HistoryTradeMngVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 结算报告
 * @author pjc
 */
@RestController
@RequestMapping("/settlementReport")
@Api(tags = "结算报告")
public class SettlementReportController {

    @Resource
    SettlementReportService settlementReportService;

    @Resource
    private TradeRiskInfoService tradeRiskInfoService;

    @Resource
    private TradeCloseMngService tradeCloseMngService;

    @Resource
    private CapitalRecordsService capitalRecordsService;

    /**
     * 账户状况
     * @param settlementReportDTO 客户信息
     * @return 账户状况
     */
    @ApiOperation("账户状况")
    @PostMapping("/accountOverview")
    public HttpResourceResponse<AccountOverviewVO> accountOverview(@RequestBody @Valid SettlementReportDTO settlementReportDTO) {
        LocalDate localDate =   LocalDate.of(2024,1,8);
        BussinessException.E_300102.assertTrue(settlementReportDTO.getStartDate().isAfter(localDate),"不允许选择2024年1月8日前的日期");
        return HttpResourceResponse.success(settlementReportService.accountOverview(settlementReportDTO));
    }
    /**
     * 累计汇总
     * @param settlementReportDTO 客户信息
     * @return 持仓明细
     */
    @PostMapping("/getAccSummary")
    @ApiOperation("累计汇总")
    public HttpResourceResponse<List<AccSummaryVO>> getAccSummary(@RequestBody @Valid SettlementReportDTO settlementReportDTO) {
        return HttpResourceResponse.success(tradeRiskInfoService.getAccSummaryList(settlementReportDTO));
    }
    /**
     * 持仓明细
     * @param settlementReportDTO 客户信息
     * @return 持仓明细
     */
    @PostMapping("/getRiskInfoListByPage")
    @ApiOperation("持仓明细")
    public HttpResourceResponse<IPage<TradeRiskInfoVo>> getRiskInfoListByPage(@RequestBody @Valid SettlementReportDTO settlementReportDTO) {
        return HttpResourceResponse.success(tradeRiskInfoService.getRiskInfoListByPage(settlementReportDTO));
    }

    /**
     * 历史交易分页查询
     * @param settlementReportDTO  客户信息
     * @return 历史交易
     */
    @PostMapping("/historyTrade")
    @ApiOperation("历史交易分页查询")
    @CheckPermission("quote::trade/close::historyTrade")
    public HttpResourceResponse<Page<HistoryTradeMngVO>> historyTrade(@RequestBody @Valid SettlementReportDTO settlementReportDTO){
        return HttpResourceResponse.success(tradeCloseMngService.historyTradeByPage(settlementReportDTO));
    }

    /**
     * 资金记录分页查询
     * @param settlementReportDTO 客户信息
     * @return 历史交易
     */
    @ApiOperation("资金记录分页查询")
    @PostMapping("/getListByPage")
    public HttpResourceResponse<IPage<CapitalRecordsVO>> getListByPage(@RequestBody @Valid SettlementReportDTO settlementReportDTO){
        return HttpResourceResponse.success(capitalRecordsService.getListByClientPage(settlementReportDTO));
    }
    /**
     * 资金监控分页查询
     * @param capitalMonitorDTO 客户信息
     * @return 资金监控
     */
    @ApiOperation("资金监控分页查询")
    @PostMapping("/getCapitalMonitorListByPage")
    public HttpResourceResponse<IPage<CapitalMonitorVO>> getCapitalMonitorListByPage(@RequestBody @Valid CapitalMonitorDTO capitalMonitorDTO){
        return HttpResourceResponse.success(settlementReportService.getCapitalMonitorListByPage(capitalMonitorDTO));
    }
    /**
     * 资金监控导出
     * @param capitalMonitorDTO 客户信息
     */
    @ApiOperation("资金监控导出")
    @PostMapping("/exportCapitalMonitor")
    public void exportCapitalMonitor(@RequestBody @Valid CapitalMonitorDTO capitalMonitorDTO, HttpServletResponse response) throws IOException {
        settlementReportService.exportCapitalMonitor(capitalMonitorDTO,response);
    }
    /**
     * 结算报告导出
     * @param settlementReportDTO 结算报告请求参数
     * @param response 文件流
     * @throws Exception IO异常
     */
    @PostMapping("/report")
    public void export(@RequestBody @Valid SettlementReportDTO settlementReportDTO, HttpServletResponse response) throws Exception {
        settlementReportService.export(settlementReportDTO,response);
    }

    /**
     * 累计汇总批量导出
     * @param exportAllAccSummaryDTO 结算报告请求参数
     * @param response 文件流
     * @throws Exception IO异常
     */
    @PostMapping("/exportAllAccSummary")
    public void exportAllAccSummary(@RequestBody @Valid ExportAllAccSummaryDTO exportAllAccSummaryDTO, HttpServletResponse response) throws Exception {
        settlementReportService.exportAllAccSummary(exportAllAccSummaryDTO,response);
    }

    /**
     * 获取邮件通配符对应结果(不包含追保金额)
     * @param clientId 客户ID
     * @return 返回map
     */
    @GetMapping("/getMailKeywordsConfig")
    public HttpResourceResponse<Map<String, String>> getMailKeywordsConfig(@RequestParam  Integer clientId){
        return HttpResourceResponse.success(settlementReportService.getMailKeywordsConfig(clientId));
    }

    /**
     * 发送报告
     * @param dto 入参
     * @param response 响应
     * @return 返回提示信息
     * @throws Exception 异常
     */
    @PostMapping("/sendMail")
    public HttpResourceResponse<String> sendMail(@RequestBody MailDTO dto, HttpServletResponse response) throws Exception {
        settlementReportService.exportToFile(dto,response);
        return HttpResourceResponse.success("发送成功");
    }
}
