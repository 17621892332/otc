package org.orient.otc.quote.dto.settlementReport;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author chengqiang
 */
@Data
public class MailKeywordsConfigResultDto {
    /**
     * 客户ID
     */
    @NotNull(message = "客户不能为空")
    private Integer clientId;

    /**
     * 开始日期
     */
    private LocalDate startDate;
    /**
     * 查询日期(或结束日期)
     */
    @NotNull(message = "查询日期不能为空")
    private LocalDate queryDate;

}
