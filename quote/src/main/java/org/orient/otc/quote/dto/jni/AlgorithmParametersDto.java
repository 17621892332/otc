package org.orient.otc.quote.dto.jni;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.jni.enums.AlgorithmEnum;

import javax.validation.constraints.NotNull;

@Data
public class AlgorithmParametersDto {
    @ApiModelProperty(value = "计算雪球价格使用的算法",required = true)
    @NotNull
    AlgorithmEnum algorithmName;
    @ApiModelProperty(value = "蒙特卡洛模拟的路径数量",required = true)
    @NotNull
    int mcNumberPaths;
    @ApiModelProperty(value = "偏微分方程网格时间维度的格点数量",required = true)
    @NotNull
    int pdeTimeGrid;
    @ApiModelProperty(value = "偏微分方程网格状态维度（标的价格）的格点数量",required = true)
    @NotNull
    int pdeSpotGrid;
}
