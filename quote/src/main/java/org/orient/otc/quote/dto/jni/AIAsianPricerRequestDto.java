package org.orient.otc.quote.dto.jni;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 亚式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class AIAsianPricerRequestDto {
    @ApiModelProperty(value = "看涨看跌(传CALL, PUT)",required = true)
    @NotNull
    String optionType;
    @ApiModelProperty(value = "标的合约价格",required = true)
    double underlyingPrice;

    @ApiModelProperty(value = "定价时间",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NonNull
    LocalDateTime evaluationTime;

    @ApiModelProperty(value = "执行价",required = true)
    double strike;

    @ApiModelProperty(value = "到期时间点",required = true)
    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime expiryTime;

    @ApiModelProperty(value = "无风险利率",required = true)
    double riskFreeInterestRate;

    @ApiModelProperty(value = "波动率",required = true)
    double volatility;


    @NonNull
    List<ObserveScheduleDto> observeSchedule;

    @ApiModelProperty(value = "总采价次数",required = true)
    int totalObservations;


}
