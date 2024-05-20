package org.orient.otc.quote.dto.jni;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LevelDto {
    @ApiModelProperty(value = "水平值",required = true)
    @NotNull
    double levelValue;

    @ApiModelProperty(value = "是否为相对水平值",required = true)
    @NotNull
    boolean levelRelative;

    @ApiModelProperty(value = "调整值",required = true)
    @NotNull
    double levelShift;
}
