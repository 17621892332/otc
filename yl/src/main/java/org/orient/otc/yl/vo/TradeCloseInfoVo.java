package org.orient.otc.yl.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author dzrh
 */
@Data
public class TradeCloseInfoVo {
    /**
     * 了结日期，格式:yyyy-MM-dd
     */
    LocalDateTime tcValueDate;
    /**
     * 了结方式(到期|终止)
     */
    String tcAction;
    /**
     * 了结金额
     */
    BigDecimal tcAmount;
    /**
     * 了结标的价格
     */
    BigDecimal tcFinalPrice;
    /**
     * 了结数量
     */
    BigDecimal tcUnwindTradeAmount;
    /**
     * 了结数量比率
     */
    BigDecimal tcUnwindPricePercent;
    /**
     * 权利金(了结)，平仓价格
     */
    BigDecimal tcUnwindPrice;
    /**
     * 实现盈亏
     */
    BigDecimal winLoss;

}
