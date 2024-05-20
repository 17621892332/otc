package org.orient.otc.quote.vo.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("熔断累计so返回值")
public class AIKOAccumulatorPricerResultVo {
    @ApiModelProperty("估值")
    Double pv;
    @ApiModelProperty("标的价格变化对期权价值的影响")
    Double delta;
    @ApiModelProperty("标的价格变化对Delta的影响")
    Double gamma;
    @ApiModelProperty("波动率变化对期权价值的影响")
    Double vegaPercentage;
    @ApiModelProperty("时间变化对期权价值的影响")
    Double thetaPerDay;
    @ApiModelProperty("无风险利率变化对期权价值的影响")
    Double rhoPercentage;
    @ApiModelProperty("错误信息")
    String errorMessage;
    @ApiModelProperty("累购期权客户已经购入的数量；累沽期权客户已经卖出的数量")
    double accumulatedPosition;
    @ApiModelProperty("累计期权已经实现的赔付")
    double accumulatedPayment;

}
