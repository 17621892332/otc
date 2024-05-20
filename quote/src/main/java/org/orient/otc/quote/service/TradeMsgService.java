package org.orient.otc.quote.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.trade.TradeMsgQueryDto;
import org.orient.otc.quote.entity.TradeMsg;

import java.util.List;

/**
 * @author dzrh
 */
public interface TradeMsgService extends IServicePlus<TradeMsg> {

    /**
     * 通过交易ID与交易类型获取简讯
     * @return 简讯信息
     */
    TradeMsg queryMsgInfo(TradeMsgQueryDto tradeMsgQueryDto);

    boolean saveOrUpdateBatchByTradeId(List<TradeMsg> entityList);
}
