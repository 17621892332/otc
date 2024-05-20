package org.orient.otc.quote.dto.settlementReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.quote.enums.SettlemenReportSheetEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 结算报告发送邮件dto
 * @author cq
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "结算报告发送邮件dto")
public class MailDTO implements Serializable {
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;
    /**
     * 邮件模板ID
     */
    private Integer mailTemplateId;


    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    /**
     * 导出类型
     */
    Set<SettlemenReportSheetEnum> reportTypeSet;

    /**
     * 收件人列表
     */
    @ApiModelProperty(value = "收件人列表")
    List<String> receiveUserList;

    /**
     * 是否准保邮件 1:是 0: 否
     */
    Integer isAppendMail;
    /**
     * 追保金额
     */
    BigDecimal additionalPrice;

}
