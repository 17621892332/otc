package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CombCodeDTO {
    @ApiModelProperty(value = "交易组合code",required = true)
    @NotNull
    private String combCode;
}
