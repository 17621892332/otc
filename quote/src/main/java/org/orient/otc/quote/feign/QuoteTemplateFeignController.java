package org.orient.otc.quote.feign;

import org.orient.otc.api.quote.feign.QuoteTemplateClient;
import org.orient.otc.quote.service.QuoteTemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 模板控制实现
 */
@RestController
@RequestMapping(value = "/quoteTemplate")
public class QuoteTemplateFeignController implements QuoteTemplateClient {
    @Resource
    private QuoteTemplateService quoteTemplateService;



    @Override
    public Boolean deleteMaturityTemplate() {
        return quoteTemplateService.deleteMaturityTemplate();
    }
}
