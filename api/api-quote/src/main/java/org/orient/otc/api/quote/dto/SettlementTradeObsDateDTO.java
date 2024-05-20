package org.orient.otc.api.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 结算中观察日相关参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SettlementTradeObsDateDTO {
    /**
     * 结算日期 默认当前日期
     */
    private LocalDate settlementDate;
}
