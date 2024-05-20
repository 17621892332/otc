package org.orient.otc.quote.vo.trade;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户盈亏
 */
@Data
public class TradeProfitLossByClientVO {

    private Integer clientId;

    private BigDecimal profitLoss;
}
