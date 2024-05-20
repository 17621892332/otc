package org.orient.otc.quote.vo.jni;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 欧式返回值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIEnhancedAsianPricerResultVo {
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
    @ApiModelProperty("股息率变化对期权价值的影响")
    Double dividendRhoPercentage;
    @ApiModelProperty("错误信息")
    String errorMessage;
    @ApiModelProperty("累购期权客户已经购入的数量；累沽期权客户已经卖出的数量")
    double accumulatedPosition;
    @ApiModelProperty("累计期权已经实现的赔付")
    double accumulatedPayment;
}
