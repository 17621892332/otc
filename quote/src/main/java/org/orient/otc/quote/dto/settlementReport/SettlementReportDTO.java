package org.orient.otc.quote.dto.settlementReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;
import org.orient.otc.quote.enums.SettlemenReportSheetEnum;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * 结算报告分页明细参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "结算报告分页明细参数")
public class SettlementReportDTO extends BasePage implements Serializable {

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "客户ID不能为空")
    private Integer clientId;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @NotNull(message = "结束日期不能为空")
    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    /**
     * 导出类型
     */
    Set<SettlemenReportSheetEnum> reportTypeSet;

    // -------结算报告页面发送邮件使用参数 start-----------------------------------------------------------
    /**
     * 结算报告导出的临时文件, 用于结算报告的邮件发送时, 所带的结算报告附件 , 仅在结算报告页面发送邮件使用此参数
     */
    private File tempFile;
    /**
     * 报告文件名称 , 仅在结算报告页面发送邮件使用此参数
     */
    private String tempFileName;
    /**
     * 是否发送邮件请求生成结算报告文件 , 仅在结算报告页面/重发时(需要把生成的结算报告附件添加到邮件内容中去)发送邮件使用此参数
     */
    private Boolean sendMailFlag;

    // -------结算报告页面发送邮件使用参数 end-----------------------------------------------------------

}
