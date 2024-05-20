package org.orient.otc.dm.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.dm.dto.underlyingQuote.UnderlyingQuoteUpdateDTO;
import org.orient.otc.dm.entity.UnderlyingQuote;
import org.orient.otc.api.dm.vo.UnderlyingQuoteVO;

import java.util.List;

public interface UnderlyinQuoteService extends IServicePlus<UnderlyingQuote> {
    List<UnderlyingQuoteVO> getList();
    List<UnderlyingQuoteVO> getQuoteList();
    String update(List<UnderlyingQuoteUpdateDTO> dtos);
}
