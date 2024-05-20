package org.orient.otc.quote.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.quote.dto.risk.CapitalMonitorDTO;
import org.orient.otc.quote.dto.settlementReport.ExportAllAccSummaryDTO;
import org.orient.otc.quote.dto.settlementReport.MailDTO;
import org.orient.otc.quote.dto.settlementReport.MailKeywordsConfigResultDto;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.vo.settlementreport.AccountOverviewVO;
import org.orient.otc.quote.vo.settlementreport.CapitalMonitorVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 结算报告服务
 * @author pjc
 */
public interface SettlementReportService {
    /**
     * 获取账户汇总信息
     * @param settlementReportDTO 结算报告请求参数
     * @return 汇总信息
     */
    AccountOverviewVO accountOverview(SettlementReportDTO settlementReportDTO);

    /**
     * 结算报告导出
     * @param settlementReportDTO 导出请求对象
     * @param response 导出响应
     * @throws IOException IO异常
     */
    void export(SettlementReportDTO settlementReportDTO, HttpServletResponse response) throws IOException;

    /**
     * 批量导出所有累计汇总数据
     * @param exportAllAccSummaryDTO 导出请求对象
     * @param response 导出响应
     * @throws IOException IO异常
     */
    void exportAllAccSummary(ExportAllAccSummaryDTO exportAllAccSummaryDTO, HttpServletResponse response) throws IOException;

    /**
     * 资金监控导出
     * @param capitalMonitorDTO 导出参数
     * @param response 导出响应
     */
    void exportCapitalMonitor(CapitalMonitorDTO capitalMonitorDTO, HttpServletResponse response) throws IOException;
    /**
     * 结算报告导出到临时文件流
     * @param dto 发送邮件参数
     * @param response 导出响应
     * @throws IOException IO异常
     */
    void exportToFile(MailDTO dto, HttpServletResponse response) throws IOException;

    /**
     * 资金监控分页查询
     * @param capitalMonitorDTO 资金监控信息
     * @return 分页数据
     */
    IPage<CapitalMonitorVO> getCapitalMonitorListByPage(CapitalMonitorDTO capitalMonitorDTO);

    /**
     * 获取邮件通配符对应结果(不包含追保金额)
     * @param clientId 客户ID
     * @return 返回map
     */
    Map<String, String> getMailKeywordsConfig(Integer clientId);
    /**
     * 获取邮件通配符对应结果(包含追保金额)
     * @param dto 入参
     * @return 返回map
     */
    Map<String, String> getAllMailKeywordsConfig(MailKeywordsConfigResultDto dto);

}
