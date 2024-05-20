package org.orient.otc.api.message.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ReSendDto {
    /**
     * 客户ID
     */
    private Integer clientId;
    /**
     * 邮件模板ID
     */
    private Integer mailTemplateId;
    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;
    /**
     * 导出类型
     */
    Set<String> reportTypeSet;
}
