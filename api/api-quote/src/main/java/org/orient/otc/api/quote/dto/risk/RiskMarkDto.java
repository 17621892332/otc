package org.orient.otc.api.quote.dto.risk;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskMarkDto {
    @ApiModelProperty(value = "标的代码")
    @NotNull
    private String underlyingCode;
}
