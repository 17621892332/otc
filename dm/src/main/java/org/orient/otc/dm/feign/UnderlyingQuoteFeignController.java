package org.orient.otc.dm.feign;

import org.orient.otc.api.dm.feign.UnderlyingQuoteClient;
import org.orient.otc.api.dm.vo.UnderlyingQuoteVO;
import org.orient.otc.dm.service.UnderlyinQuoteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 合约内部接口
 */
@RestController
@RequestMapping("/underlyingQuote")
public class UnderlyingQuoteFeignController implements UnderlyingQuoteClient {
    @Resource
    private UnderlyinQuoteService underlyinQuoteService;



    @Override
    public List<UnderlyingQuoteVO> getUnderlyingQuoteList() {
        return underlyinQuoteService.getQuoteList();
    }
}
