package org.orient.otc.quote.dto.jni;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RateStructDto {
    @ApiModelProperty(value = "收益率，可以是返息率、红利票息率等",required = true)
    @NotNull
    double rateValue;

    @ApiModelProperty(value = "收益率是否年化",required = true)
    @NotNull
    boolean rateAnnulized;
}
