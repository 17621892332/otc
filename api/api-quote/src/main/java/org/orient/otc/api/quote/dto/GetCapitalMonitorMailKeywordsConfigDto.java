package org.orient.otc.api.quote.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author chengqiang
 */
@Data
public class GetCapitalMonitorMailKeywordsConfigDto {
    /**
     * 客户ID
     */
    private Integer clientId;

    /**
     * 查询日期(或结束日期)
     */
    private LocalDate queryDate;

}
