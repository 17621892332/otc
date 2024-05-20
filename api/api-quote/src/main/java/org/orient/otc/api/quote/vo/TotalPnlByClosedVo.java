package org.orient.otc.api.quote.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 已平仓的交易累计盈亏
 * @author dzrh
 */
@Data
public class TotalPnlByClosedVo {

    /**
     * 标的资产码
     */
    private String underlyingCode;
    /**
     * 累计盈亏
     */
    private BigDecimal totalPnl;
}
