package org.orient.otc.api.quote.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel
public class VolatityDeltaDataVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "delta")
    private String delta;
    @ApiModelProperty(value = "期限(单位天)")
    private Integer expire;
    @ApiModelProperty(value = "波动率/差值")
    private BigDecimal vol;
}
