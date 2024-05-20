package org.orient.otc.dm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UnderlyingVolatilityDelDto {
    @ApiModelProperty(value = "标的资产码")
    @NotNull
    private String underlyingCode;
}
