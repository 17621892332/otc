package org.orient.otc.api.quote.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author chengqiang
 */
@Data
public class GetSettlementReportMailKeywordsConfigDto {
    /**
     * 客户ID
     */
    private Integer clientId;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

}
