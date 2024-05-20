package org.orient.otc.quote.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.risk.ExchangeRealTimePos;
import org.orient.otc.quote.entity.ExchangePosition;

import java.util.List;

public interface ExchangePositionService extends IServicePlus<ExchangePosition> {

    /**
     * 通过交易日获取场内持仓列表
     * @param tradingDay 交易日期 yyyyMMdd
     * @return 转换为缓存响应
     */
    List<ExchangeRealTimePos> selectPositionByTradingDay(String tradingDay);

    /**
     * 获取补单对应的场内持仓信息
     * @param tradingDay 交易日期 yyyyMMdd
     * @return 转换为缓存响应
     */
    List<ExchangeRealTimePos> selectPositionBySupplementaryAndTradingDay(String tradingDay);
}
