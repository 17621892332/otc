package org.orient.otc.quote.dto.jni;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.orient.otc.common.jni.enums.AccumulatorTypeEnum;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 欧式累计
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ApiModel
public class AIAccumulatorPricerDto {

    @ApiModelProperty(value = "期权类型",required = true)
    @NotNull(message = "期权类型不能为空")
    private AccumulatorTypeEnum accumulatorType;

    @ApiModelProperty(value = "需要计算的值的序列（默认为“a”，可以是{ \"p\", \"d\", \"g\", \"v\", \"t\", \"r\", \"m\", \"AccuPosition\", \"AccuPayment\"}及其自由组合）",required = true)
    @NotNull(message = "需要计算的值的序列不能为空")
    private String valueType;

    @ApiModelProperty(value = "东证润和买卖方向（适用于增强亚式期权；如果取值为1，则为东证润和买入，客户卖出；如果取值为-1，则为东证润和卖出，客户买入；累购和累沽默认为1）",required = true)
    @NotNull(message = "东证润和买卖方向不能为空")
    private int buySell;

    @ApiModelProperty(value = "每日数量",required = true)
    @NotNull(message = "每日数量不能为空")
    private double basicQuantity;

    @ApiModelProperty(value = "标的合约价格",required = true)
    @NotNull(message = "标的合约价格不能为空")
    private double underlyingPrice;

    @ApiModelProperty(value = "执行价",required = true)
    @NotNull(message = "执行价不能为空")
    private double strike;

    @ApiModelProperty(value = "定价时点的时间",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "定价时点的时间不能为空")
    private LocalDateTime evaluationTime;

    @ApiModelProperty(value = "定价时点的时间",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "定价时点的时间不能为空")
    private LocalDateTime expiryTime;

    @ApiModelProperty(value = "常数波动率",required = true)
    @NotNull(message = "常数波动率不能为空")
    private double constantVol;

    @ApiModelProperty(value = "是否为现金结算（{0, 1}）",required = true)
    @NotNull(message = "是否为现金结算不能为空")
    private int isCashSettled;

    @ApiModelProperty(value = "无风险利率",required = true)
    @NotNull(message = "是否无风险利率不能为空")
    private double riskFreeInterestRate;

    @ApiModelProperty(value = "杠杆系数",required = true)
    @NotNull(message = "杠杆系数不能为空")
    private double leverage;

    @ApiModelProperty(value = "单位固定赔付",required = true)
    @NotNull(message = "单位固定赔付不能为空")
    private double fixedPayment;

    @ApiModelProperty(value = "敲出障碍价格",required = true)
    @NotNull(message = "敲出障碍价格不能为空")
    private double barrier;

    @ApiModelProperty(value = "执行价斜坡",required = true)
    @NotNull(message = "执行价斜坡不能为空")
    private double strikeRamp;

    @ApiModelProperty(value = "障碍价斜坡",required = true)
    @NotNull
    private double barrierRamp;

    @ApiModelProperty(value = "总采价次数，采价天数",required = true)
    @NotNull
    private int totalObservations;

    @ApiModelProperty(value = "用于对每个成分期权进行估值的波动率组成的曲面，横轴为虚实程度，纵轴为期权期限",required = true)
    @NotNull
    private VolSurfaceDto volSurface;

    @ApiModelProperty(value = "情景价格",required = true)
    @NotNull
    private double scenarioPrice;

    @NonNull
    List<ObserveScheduleDto> observeSchedule;
}
