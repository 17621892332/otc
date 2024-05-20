package org.orient.otc.netty.adapter;


import org.orient.otc.api.quote.dto.risk.TradeRiskCacularResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存数据
 */
public class ConcurrentAdapter {
    /**
     * 行情系统
     */
    public static ConcurrentHashMap<String, TradeRiskCacularResult> totalPnlByClosedMap = new ConcurrentHashMap<>();
}
