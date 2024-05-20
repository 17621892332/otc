package org.orient.otc.dm.dto.variety;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class VarietyIdDto {
    @ApiModelProperty(value = "品种id",required = true)
    @NotNull
    private Integer varietyId;
}
