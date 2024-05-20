package org.orient.otc.openapi.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MarketDataDto {
    @NotEmpty(message = "标的code不能为空")
    @ApiModelProperty(value = "标的code",required = true)
    String underlyingCode;
}
