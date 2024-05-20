package org.orient.otc.market.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class MarketCloseData implements Serializable {
    private static final long serialVersionUID = 1L;

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
