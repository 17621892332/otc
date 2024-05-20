package org.orient.otc.quote.dto.risk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 结算报告持仓明细
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "结算报告持仓明细")
public class TradeRiskInfoPageDTO extends BasePage implements Serializable {
    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "客户ID不能为空")
    private Integer clientId;

    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

}
