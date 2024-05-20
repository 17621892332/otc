package org.orient.otc.quote.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.quote.QuoteTemplateDto;
import org.orient.otc.quote.dto.quote.QuoteTempleIdDto;
import org.orient.otc.quote.entity.QuoteTemplate;
import org.orient.otc.quote.vo.template.QuoteTemplateContentVO;

import java.util.List;

/**
 * 模板服务
 */
public interface QuoteTemplateService extends IServicePlus<QuoteTemplate> {

    /**
     * 新增模板
     * @param quoteTemplateDto 模板参数
     * @return 是否成功
     */
    String insertTemplate(QuoteTemplateDto quoteTemplateDto);
    /**
     * 删除模板
     * @param quoteTempleIdDto 模板ID
     * @return 是否成功
     */
    String deleteTemple(QuoteTempleIdDto quoteTempleIdDto);

    /**
     * 获取模板列表
     * @return 模板列表
     */
    List<QuoteTemplate> getQuoteTemplateList();

    /**
     * 通过模板ID获取模板内容
     * @param quoteTempleIdDto 模板ID
     * @return 模板内容
     */
    List<QuoteTemplateContentVO> getQuoteTemplateContentByTemplateId(QuoteTempleIdDto quoteTempleIdDto);

    /**
     * 删除到期模板
     * @return 是否成功
     */
    Boolean  deleteMaturityTemplate();
}
