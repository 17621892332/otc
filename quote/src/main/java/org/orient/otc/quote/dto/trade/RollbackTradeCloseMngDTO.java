package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 平仓回退信息
 */
@Data
@ApiModel
public class RollbackTradeCloseMngDTO {
    @ApiModelProperty(value = "平仓ID",required = true)
    @NotNull(message = "平仓ID不能为空")
    private Integer tradeCloseMngId;
}
