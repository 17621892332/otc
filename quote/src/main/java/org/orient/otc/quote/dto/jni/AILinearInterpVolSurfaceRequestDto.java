package org.orient.otc.quote.dto.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 线性插值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class AILinearInterpVolSurfaceRequestDto {

    @ApiModelProperty(value = "需计算的波动率所在的期限",required = true)
    @NotNull
    Double dimTenor;

    @ApiModelProperty(value = "需计算的波动率所在的虚实值位置",required = true)
    @NotNull
    Double dimMoneyness;

    @ApiModelProperty(value = "用于对每个成分期权进行估值的波动率组成的曲面，横轴为虚实程度，纵轴为期权期限",required = true)
    @NotNull
    private VolSurfaceDto volSurface;
}
