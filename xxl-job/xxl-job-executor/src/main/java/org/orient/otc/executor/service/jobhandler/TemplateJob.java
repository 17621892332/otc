package org.orient.otc.executor.service.jobhandler;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.orient.otc.api.quote.feign.QuoteTemplateClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 模板定时任务
 */
@Component
public class TemplateJob {

    @Resource
    QuoteTemplateClient quoteTemplateClient;
    /**
     * 每天清理到期模板
     * @return 清理状态
     */
    @XxlJob("deleteMaturityTemplate")
    public Boolean deleteMaturityTemplate() {
        return quoteTemplateClient.deleteMaturityTemplate();
    }
}
