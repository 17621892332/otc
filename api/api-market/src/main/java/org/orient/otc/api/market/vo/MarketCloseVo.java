package org.orient.otc.api.market.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketCloseVo {

    /**
     * 合约代码
     */
    private String instrumentID;
    /**
     * 最低价
     */
    private BigDecimal lowestPrice;
    /**
     * 最高价
     */

    private BigDecimal highestPrice;

    /**
     * 收盘价
     */

    private BigDecimal closePrice;
    /**
     * 结算价
     */

    private BigDecimal settlementPrice;
    /**
     * 交易日
     */

    private String tradingDay;
}
