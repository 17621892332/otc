package org.orient.otc.yl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新保证金参数
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTradePositionMarginDTO {

    /**
     * 交易编号
     */
    private String tradeNumber;

    /**
     * 估值日期: yyyy-MM-dd
     */
    private LocalDate valueDate;

    /**
     * 持仓保证金
     */
    private BigDecimal margin;
}
