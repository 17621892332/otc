package org.orient.otc.api.quote.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel
public class VolatityDataDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "行权价")
    private BigDecimal strike;
    @ApiModelProperty(value = "期限(单位天)")
    private Integer expire;
    @ApiModelProperty(value = "波动率/差值")
    private BigDecimal vol;
}
