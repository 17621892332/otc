package org.orient.otc.api.market.vo;

import lombok.Data;
import org.orient.otc.common.core.config.BigDecimalFormatter;

import java.math.BigDecimal;

/**
 * 行情信息
 */
@Data
public class MarketInfoVO {
    /**
     * 最新价格
     */
    @BigDecimalFormatter
    private BigDecimal lastPrice;
    /**
     * 行情时间
     */
    private String updateTime;
    /**
     * 合约代码
     */
    private String instrumentId;
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
