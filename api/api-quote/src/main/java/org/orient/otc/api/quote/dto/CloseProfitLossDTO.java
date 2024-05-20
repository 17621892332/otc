package org.orient.otc.api.quote.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * 盈亏和估值
 */
@Data
public class CloseProfitLossDTO {

    @ApiModelProperty(value = "客户ID")
    private Set<Integer> clientIdList;

    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;
}
