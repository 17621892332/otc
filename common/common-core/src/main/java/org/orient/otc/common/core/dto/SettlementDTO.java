package org.orient.otc.common.core.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 结算参数
 */
@Data
public class SettlementDTO {
    /**
     * 结算日期 默认当前日期
     */
    private LocalDate settlementDate;
}
