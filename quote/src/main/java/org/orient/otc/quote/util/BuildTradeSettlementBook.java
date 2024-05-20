package org.orient.otc.quote.util;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.quote.config.TemplateConfig;
import org.orient.otc.quote.dto.confirmbook.VanillaPricerConfirmBookDTO;
import org.orient.otc.quote.dto.settlementbook.SettlementBook;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * 构建交易确认书
 * @author dzrh
 */
@Component
public class BuildTradeSettlementBook {
    @Resource
    TemplateConfig templateConfig;

    @Resource
    WordUtil wordUtil;


    /**
     * 处理亚式期权(看涨看跌) , 增强亚式(看涨看跌) 一笔交易生成一个确认书
     * @param settlementBook 交易信息
     */
    public ByteArrayInputStream generateSettlementBook(SettlementBook settlementBook) throws Exception {
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure configure = Configure.builder()
                .bind("data", policy).build();
        return wordUtil.exportInputStream(templateConfig.getSettlementConfirmBookPath() + settlementBook.getSettlementBookType().name() + ".docx"
                , configure, settlementBook);
    }
}
