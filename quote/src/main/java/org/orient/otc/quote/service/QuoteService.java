package org.orient.otc.quote.service;

import org.orient.otc.quote.dto.quote.QuotationDTO;
import org.orient.otc.quote.dto.quote.QuoteCalculateDTO;
import org.orient.otc.quote.vo.quote.QuotationVO;
import org.orient.otc.quote.vo.quote.QuoteStringResultVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 定价计算服务
 */
public interface QuoteService {
    /**
     * 定价计算
     * @param quoteDto  定价参数
     * @return 计算结果
     */
    List<QuoteStringResultVo> quote(QuoteCalculateDTO quoteDto) ;

    /**
     * 报价预览
     * @param quotationDTO 报价日期
     * @return 报价结果
     */
    QuotationVO quotationList(QuotationDTO quotationDTO);
    /**
     * 报价预览导出
     * @param quotationDTO 报价日期
     * @param response  响应
     */
    void quotationReport(QuotationDTO quotationDTO, HttpServletResponse response) throws IOException;
}
