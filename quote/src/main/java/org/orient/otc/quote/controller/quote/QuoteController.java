package org.orient.otc.quote.controller.quote;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.quote.QuotationDTO;
import org.orient.otc.quote.dto.quote.QuoteCalculateDTO;
import org.orient.otc.quote.service.QuoteService;
import org.orient.otc.quote.vo.quote.QuotationVO;
import org.orient.otc.quote.vo.quote.QuoteStringResultVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 报价
 */
@RestController
@RequestMapping("/quote")
@Api(tags = "报价")
@Validated
public class QuoteController {
    @Resource
    private QuoteService quoteService;

    /**
     * 定价计算
     * @param quoteDto 定价参数
     * @return 计算结果
     */
    @PostMapping("/calculate")
    @ApiOperation("报价计算")
    @CheckPermission("quote::quote::calculate")
    public HttpResourceResponse<List<QuoteStringResultVo>> quote(@RequestBody @Valid QuoteCalculateDTO quoteDto) {
        return HttpResourceResponse.success(quoteService.quote(quoteDto));
    }
    /**
     * 报价预览
     * @param quotationDTO 报价参数
     * @return 计算结果
     */
    @PostMapping("/quotation")
    @ApiOperation("报价预览")
    @CheckPermission("quote::quote::quotation")
    public HttpResourceResponse<QuotationVO> quotation(@RequestBody QuotationDTO quotationDTO) {
        return HttpResourceResponse.success(quoteService.quotationList(quotationDTO));
    }

    /**
     * 报价预览导出
     * @param quotationDTO 报价参数
     * @return 计算结果
     */
    @PostMapping("/quotationReport")
    @ApiOperation("报价预览")
    @CheckPermission("quote::quote::quotationReport")
    public void quotationReport(@RequestBody QuotationDTO quotationDTO, HttpServletResponse response) throws IOException {
        quoteService.quotationReport(quotationDTO,response);
    }
}
