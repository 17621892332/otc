package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.quote.dto.trade.ExchangeTradePageListDto;
import org.orient.otc.quote.vo.ExchangeTradeVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExchangeTradeService {

    IPage<ExchangeTradeVo> selectOptionListByPage(ExchangeTradePageListDto dto);

    void tradeExport(ExchangeTradePageListDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
