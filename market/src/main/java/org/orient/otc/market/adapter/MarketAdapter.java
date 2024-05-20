package org.orient.otc.market.adapter;


import org.orient.otc.api.market.vo.MarketInfoVO;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 行情系统
 */
public class MarketAdapter {
    /**
     * 行情系统
     */
    public static ConcurrentHashMap<String, MarketInfoVO> marketData = new ConcurrentHashMap<>();
}
