package org.orient.otc.quote.dto.jni;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 香草
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class AIVanillaPricerRequestDto {
    @ApiModelProperty(value = "看涨看跌(传CALL, PUT)",required = true)
    @NotNull
    String optionType;
    @ApiModelProperty(value = "标的合约价格",required = true)
    @NotNull
    Double underlyingPrice;
    @ApiModelProperty(value = "执行价",required = true)
    @NotNull
    Double strike;
    @ApiModelProperty(value = "定价时点",required = true)
    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime evaluationTime;
    @ApiModelProperty(value = "到期时间点",required = true)
    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime expiryTime;
    @ApiModelProperty(value = "无风险利率",required = true)
    @NotNull
    Double riskFreeInterestRate;
    @ApiModelProperty(value = "股息率",required = true)
    @NotNull
    Double dividendYield;

    @ApiModelProperty(value = "波动率",required = true)
    @NotNull
    Double volatility;
}
