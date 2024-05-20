package org.orient.otc.api.quote.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnderlyingVolatilityFeignDto {
    @ApiModelProperty(value = "标的资产码")
    private String underlyingCode;
    @ApiModelProperty(value = "是否是主力合约")
    private Boolean mainContract;
    @ApiModelProperty(value = "波动率偏移量")
    private BigDecimal volOffset;
}
