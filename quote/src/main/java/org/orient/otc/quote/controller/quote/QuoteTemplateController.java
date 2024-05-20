package org.orient.otc.quote.controller.quote;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.quote.QuoteTemplateDto;
import org.orient.otc.quote.dto.quote.QuoteTempleIdDto;
import org.orient.otc.quote.entity.QuoteTemplate;
import org.orient.otc.quote.service.QuoteTemplateService;
import org.orient.otc.quote.vo.template.QuoteTemplateContentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 定价模板
 */
@RestController
@RequestMapping("/quoteTemplate")
@Api(tags = "定价模板")
public class QuoteTemplateController {
    @Autowired
    QuoteTemplateService quoteTemplateService;

    /**
     * 新增定价模板
     * @param quoteTemplateDto 模板参数
     * @return 是否成功
     */
    @PostMapping("/insert")
    @ApiOperation("新增定价模板")
    @CheckPermission("quote::quoteTemplate::insert")
    public HttpResourceResponse<String> insertTemplate(@RequestBody @Valid QuoteTemplateDto quoteTemplateDto){
        return HttpResourceResponse.success(quoteTemplateService.insertTemplate(quoteTemplateDto));
    }

    /**
     * 删除定价模板
     * @param quoteTempleIdDto 模板ID
     * @return 是否成功
     */
    @PostMapping("/delete")
    @ApiOperation("删除定价模板")
    @CheckPermission("quote::quoteTemplate::delete")
    public HttpResourceResponse<String> deleteTemple(@RequestBody @Valid QuoteTempleIdDto quoteTempleIdDto){
        return HttpResourceResponse.success(quoteTemplateService.deleteTemple(quoteTempleIdDto));
    }

    /**
     * 获取定价模板列表
     * @return 模板列表
     */
    @PostMapping("/list")
    @ApiOperation("获取定价模板列表")
    @CheckPermission("quote::quoteTemplate::list")
    public HttpResourceResponse<List<QuoteTemplate>> getQuoteTemplateList(){
        return HttpResourceResponse.success(quoteTemplateService.getQuoteTemplateList());
    }

    /**
     * 通过模板id获取模板定价内容
     * @param quoteTempleIdDto 模板ID
     * @return 模板列表
     */
    @PostMapping("/getQuoteTemplateContentByTemplateId")
    @ApiOperation("通过模板id获取模板定价内容")
    @CheckPermission("quote::quoteTemplate::getQuoteTemplateContentByTemplateId")
    public HttpResourceResponse<List<QuoteTemplateContentVO>> getQuoteTemplateContentByTemplateId(@RequestBody @Valid QuoteTempleIdDto quoteTempleIdDto){
        return HttpResourceResponse.success(quoteTemplateService.getQuoteTemplateContentByTemplateId(quoteTempleIdDto));
    }
}
