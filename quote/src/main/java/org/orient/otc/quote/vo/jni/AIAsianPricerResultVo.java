package org.orient.otc.quote.vo.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("亚式so返回值")
public class AIAsianPricerResultVo {
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
    String message;

    public AIAsianPricerResultVo(Double pv, Double delta, Double gamma, Double vegaPercentage, Double thetaPerDay, Double rhoPercentage, String message) {
        this.pv = pv;
        this.delta = delta;
        this.gamma = gamma;
        this.vegaPercentage = vegaPercentage;
        this.thetaPerDay = thetaPerDay;
        this.rhoPercentage = rhoPercentage;
        this.message = message;
    }
}
