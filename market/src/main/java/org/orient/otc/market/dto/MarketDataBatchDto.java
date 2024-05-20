package org.orient.otc.market.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class MarketDataBatchDto {
    @NotEmpty(message = "标的code集合不能为空")
    @ApiModelProperty(value = "标的code",required = true)
    Set<String> underlyingCodeList;
}
