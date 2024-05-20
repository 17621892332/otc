package org.orient.otc.quote.dto.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orient.otc.common.jni.dto.*;
import org.orient.otc.quote.enums.SoTypeEnum;

import javax.validation.constraints.NotNull;

/**
 * SO测试参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SoRequestDto {
    @ApiModelProperty(value = "交易序号",required = true)
    @NotNull
    private int tradeNo;
    @ApiModelProperty(value = "so类型",required = true)
    @NotNull
    private SoTypeEnum soType;
    @ApiModelProperty(value = "香草参数")
    private AIVanillaPricerRequestDto aiVanillaPricerRequest;

    @ApiModelProperty(value = "远期参数")
    private AIForwardPricerRequestDto aiForwardPricerRequest;

    @ApiModelProperty(value = "亚式参数")
    private AIAsianPricerRequestDto aiAsianPricerRequest;

    @ApiModelProperty(value = "增强亚式参数")
    private AIEnhancedAsianPricerRequestDto aiEnhancedAsianPricerRequestDto;

    @ApiModelProperty(value = "欧式累计参数")
    private AIAccumulatorPricerDto aiAccumulatorPricerRequest;

    @ApiModelProperty(value = "熔断累计参数")
    private AIKOAccumulatorPricerDto aikoAccumulatorPricerDto;
    @ApiModelProperty(value = "雪球参数")
    private AISnowBallPricerDto aiSnowBallPricerRequest;
    @ApiModelProperty(value = "隐含波动率参数")
    private AIBlackImpliedVolRequestDto aiBlackImpliedVolRequest;

    @ApiModelProperty(value = "线性插值")
    private AILinearInterpVolSurfaceRequestDto aiLinearInterpVolSurfaceRequest;
    @ApiModelProperty(value = "波动率曲面转换")
    private  VolSurfaceDto aIDeltaVol2StrikeVolRequest;

    @ApiModelProperty(value = "保险亚式计算参数")
    private AIInsuranceAsianPricerRequest aiInsuranceAsianPricerRequest;

    @ApiModelProperty(value = "折价建仓雪球参数")
    private AIDisOpenSnowBallPricerRequest aiDisOpenSnowBallPricerRequest;

    @ApiModelProperty(value = "单障碍期权参数")
    private AISingleBarrierRequest aiSingleBarrierRequest;

    private ObserveSchedule[] observeSchedule;
    private VolSurface volSurface;
    private DivTermStructure divTermStructure;

}
