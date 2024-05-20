package org.orient.otc.quote.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 模板文件目录
 * @author dzrh
 */
@Configuration
@Data
@RefreshScope
public class TemplateConfig {
    /**
     * 交易确认书模板路径
     */
    @Value("${confirmTemplate.trade}")
    String tradeConfirmBookPath;

    /**
     * 结算确认书模板路径
     */
    @Value("${confirmTemplate.settlement}")
    String settlementConfirmBookPath;
}
