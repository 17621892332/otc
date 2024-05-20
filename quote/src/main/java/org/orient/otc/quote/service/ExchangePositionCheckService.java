package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.quote.dto.trade.ExchangePositionCheckPageListDto;
import org.orient.otc.quote.vo.ExchangePositionCheckVo;
import org.orient.otc.quote.vo.ExchangeTradeVo;

public interface ExchangePositionCheckService {

    IPage<ExchangePositionCheckVo> selectOptionListByPage(ExchangePositionCheckPageListDto dto);
}
