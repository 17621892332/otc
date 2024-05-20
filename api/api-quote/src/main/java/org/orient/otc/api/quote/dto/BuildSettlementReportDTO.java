package org.orient.otc.api.quote.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class BuildSettlementReportDTO implements Serializable {

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @NotNull(message = "结束日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * 导出类型
     */
    Set<String> reportTypeSet;

    // -------结算报告页面发送邮件使用参数 start-----------------------------------------------------------
    /**
     * 结算报告导出的临时文件, 用于结算报告的邮件发送时, 所带的结算报告附件 , 仅在结算报告页面发送邮件使用此参数
     */
    private File tempFile;
    /**
     * 报告文件名称 , 仅在结算报告页面发送邮件使用此参数
     */
    private String tempFileName;
    // -------结算报告页面发送邮件使用参数 end-----------------------------------------------------------

    String authorizeInfo; // 登录信息

}
