package org.orient.otc.quote.dto.jni;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 欧式
 */
@Data
@NoArgsConstructor(force = true)
public class AIEnhancedAsianPricerRequestDto {
    /**
     * 看涨看跌
     */
    @ApiModelProperty(value = "看涨看跌",required = true)
    @NotNull
    private String optionType;

    /**
     * 标的合约价格
     */
    @ApiModelProperty(value = "标的合约价格",required = true)
    @NotNull
    private Double underlyingPrice;

    /**
     * 执行价
     */
    @ApiModelProperty(value = "执行价",required = true)
    @NotNull
    private Double strike;

    /**
     * 定价时点的时间戳
     */
    @ApiModelProperty(value = "定价时点的时间戳",required = true)
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluationTime;
    /**
     * 到期时间点的时间戳
     */
    @ApiModelProperty(value = "到期时间点的时间戳",required = true)
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryTime;
    /**
     * 常数波动率
     */
    @ApiModelProperty(value = "常数波动率",required = true)
    @NotNull
    private Double constantVol;
    /**
     * 需要计算的值的序列
     * 默认为“a”，可以是{ "p", "d", "g", "v", "t", "r", "m", "AccuPosition", "AccuPayment"}及其自由组合
     */
    @ApiModelProperty(value = "需要计算的值的序列（默认为“a”，可以是{ \"p\", \"d\", \"g\", \"v\", \"t\", \"r\", \"m\", \"AccuPosition\", \"AccuPayment\"}及其自由组合）",required = true)
    @NotNull
    private String valueType;

    /**
     * 总采价次数，采价天数
     */
    @ApiModelProperty(value = "总采价次数，采价天数",required = true)
    @NotNull
    private Integer totalObservations;

    /**
     * 是否为现金结算
     * {0, 1}
     */
    @ApiModelProperty(value = "是否为现金结算",required = true)
    @NotNull
    private Integer isCashSettled;

    /**
     * 无风险利率
     */
    @ApiModelProperty(value = "无风险利率",required = true)
    @NotNull
    private Double riskFreeInterestRate;


    /**
     * 用于对每个成分期权进行估值的波动率组成的曲面，横轴为虚实程度，纵轴为期权期限
     */
    @ApiModelProperty(value = "用于对每个成分期权进行估值的波动率组成的曲面，横轴为虚实程度，纵轴为期权期限",required = true)
    @NotNull
    private VolSurfaceDto volSurface;

    /**
     * 情景价格
     */
    @ApiModelProperty(value = "情景价格",required = true)
    @NotNull
    private Double scenarioPrice;

    @NonNull
    List<ObserveScheduleDto> observeSchedule;
}
