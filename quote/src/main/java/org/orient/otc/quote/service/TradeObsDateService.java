package org.orient.otc.quote.service;

import org.orient.otc.api.quote.dto.SettlementTradeObsDateDTO;
import org.orient.otc.quote.entity.TradeObsDate;

import java.util.List;

public interface TradeObsDateService {
    /**
     * 通过交易日期与交易类型获取需要敲出的交易编号
     * @param dto 期权类型与敲出日期
     * @return 交易编号
     */
    List<String> getNeedKnockOutTradeCodeList(SettlementTradeObsDateDTO dto);

    /**
     * 获取交易的观察日列表
     * @param tradeIdList 交易记录
     * @return 观察日列表
     */
    List<TradeObsDate> getTradeObsDateListByTradeIdList(List<Integer> tradeIdList);

}
