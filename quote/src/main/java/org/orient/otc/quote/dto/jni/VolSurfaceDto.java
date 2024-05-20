package org.orient.otc.quote.dto.jni;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class VolSurfaceDto {

    @ApiModelProperty(value = "波动率曲面横轴所在的内存位置",required = true)
    @NotNull
    List<Double> horizontalAxis;

    @ApiModelProperty(value = "波动率曲面横轴的长度",required = true)
    @NotNull
    int horizontalAxisLength;

    @ApiModelProperty(value = "波动率曲面纵轴所在的内存位置",required = true)
    @NotNull
    List<Double> verticalAxis;

    @ApiModelProperty(value = "波动率曲面纵轴的长度",required = true)
    @NotNull
    int verticalAxisLength;

    @ApiModelProperty(value = "拉平的波动率曲面所在的内存位置",required = true)
    @NotNull
    List<Double> flattenedVol;

    @ApiModelProperty(value = "拉平的波动率曲面长度",required = true)
    @NotNull
    int flattenedVolLength;
}
