package org.orient.otc.quote.vo.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("线性插值返回值")
public class AILinearInterpVolSurfaceResultVo {
    @ApiModelProperty("计算出的波动率值")
    Double volatility;
    @ApiModelProperty("错误信息")
    String errorMessage;
}
