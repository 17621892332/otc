package org.orient.otc.quote.dto.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class AIForwardPricerRequestDto {
    @ApiModelProperty(value = "标的合约价格",required = true)
    @NotNull
    Double underlyingPrice;
    @ApiModelProperty(value = "执行价",required = true)
    @NotNull
    Double strike;
}
