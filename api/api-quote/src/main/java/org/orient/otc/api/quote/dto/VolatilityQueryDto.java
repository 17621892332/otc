package org.orient.otc.api.quote.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel
public class VolatilityQueryDto {
    @ApiModelProperty(value = "标的code")
    private String underlyingCode;

    @ApiModelProperty(value = "报价日期")
    private LocalDate quotationDate;
}
