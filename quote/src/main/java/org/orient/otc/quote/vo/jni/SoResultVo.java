package org.orient.otc.quote.vo.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orient.otc.common.jni.dto.VolSurface;
import org.orient.otc.common.jni.vo.AIDisOpenSnowBallPricerResult;
import org.orient.otc.common.jni.vo.AIInsuranceAsianPricerResult;
import org.orient.otc.common.jni.vo.AISingleBarrierResult;
import org.orient.otc.quote.enums.SoTypeEnum;

@AllArgsConstructor
@Data
@NoArgsConstructor
@ApiModel
public class SoResultVo {
    @ApiModelProperty(value = "交易序号",required = true)
    private int tradeNo;
    @ApiModelProperty(value = "so类型",required = true)
    private SoTypeEnum soType;
    @ApiModelProperty(value = "香草返回结果")
    private AIVanillaPricerResultVo aiVanillaPricerResult;
    @ApiModelProperty(value = "远期返回结果")
    private AIForwardPricerResultVo aiForwardPricerResult;
    @ApiModelProperty(value = "亚式返回结果")
    private AIAsianPricerResultVo aiAsianPricerResult;
    @ApiModelProperty(value = "欧式累计返回结果")
    private AIAccumulatorPricerResultVo aiAccumulatorPricerResult;
    @ApiModelProperty(value = "熔断累计返回结果")
    private AIKOAccumulatorPricerResultVo aikoAccumulatorPricerResultVo;
    @ApiModelProperty(value = "雪球返回结果")
    private AISnowBallPricerResultVo aiSnowBallPricerResult;
    @ApiModelProperty(value = "隐含波动率返回值")
    private AIBlackImpliedVolResultVo aiBlackImpliedVolResult;
    @ApiModelProperty(value = "线性插值返回值")
    private AILinearInterpVolSurfaceResultVo aiLinearInterpVolSurfaceResult;
    @ApiModelProperty(value = "波动率曲面转换")
    private VolSurface aIDeltaVol2StrikeVolResult;
    @ApiModelProperty(value = "增强亚式返回结果")
    private AIEnhancedAsianPricerResultVo aiEnhancedAsianPricerResultVo;

    @ApiModelProperty(value = "保险亚式返回结果")
    private AIInsuranceAsianPricerResult aiInsuranceAsianPricerResult;
    @ApiModelProperty(value = "折价建仓雪球返回结果")
    private AIDisOpenSnowBallPricerResult aiDisOpenSnowBallPricerResult;
    @ApiModelProperty(value = "单障碍期权返回结果")
    private AISingleBarrierResult aiSingleBarrierResult;
}
