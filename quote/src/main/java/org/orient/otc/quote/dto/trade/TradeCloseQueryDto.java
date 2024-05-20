package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author dzrh
 */
@Data
public class TradeCloseQueryDto {
    @ApiModelProperty(value = "组合代码",required = true)
    @NotEmpty(message = "组合代码不能为空")
    private String combCode;
}
