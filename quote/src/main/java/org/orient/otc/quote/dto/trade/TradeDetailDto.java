package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TradeDetailDto {
    @ApiModelProperty(value = "交易编号",required = true)
    @NotNull
    private String tradeCode;
}
