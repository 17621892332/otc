package org.orient.otc.quote.vo.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("隐含波动率返回值")
public class AIBlackImpliedVolResultVo {
    @ApiModelProperty("期权现值对应的隐含波动率")
    Double volatility;

    @ApiModelProperty("错误信息")
    String errorMessage;
}
