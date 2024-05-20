package org.orient.otc.quote.dto.risk;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 重算风险Pnl参数
 */
@Data
public class ReSetRiskPnlDTO {

    /**
     * 风险日期
     */
    private LocalDate riskDate;

    /**
     * 是否包含后续交易日
     */
    private Boolean isHavingNext;
}
