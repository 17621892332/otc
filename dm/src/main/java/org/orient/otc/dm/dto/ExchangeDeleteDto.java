package org.orient.otc.dm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExchangeDeleteDto {

    @ApiModelProperty("ID")
    @NotNull
    private Integer id;
}
