package org.orient.otc.market.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 合约代码
 */
@Data
public class MarketDataDTO {
    @NotEmpty(message = "合约代码不能为空")
    @ApiModelProperty(value = "合约代码",required = true)
    String underlyingCode;
}
