package org.orient.otc.quote.dto.jni;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 雪球
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class AISnowBallPricerDto {
    @ApiModelProperty(value = "计算雪球价格使用的算法",required = true)
    @NotNull
    AlgorithmParametersDto algorithmParameters;

    /**
     * 看涨看跌 {CALL, PUT}
     */
    @ApiModelProperty(value = "看涨看跌(传CALL, PUT)",required = true)
    @NotNull
    String optionType;

    @ApiModelProperty(value = "定价时间",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime evaluationTime;

    @ApiModelProperty(value = "定价时标的合约价格",required = true)
    @NotNull
    double underlyingPrice;

    @ApiModelProperty(value = "无风险利率",required = true)
    @NotNull
    double riskFreeInterestRate;

    @ApiModelProperty(value = "股息率",required = true)
    @NotNull
    double dividendYield;

    @ApiModelProperty(value = "波动率",required = true)
    @NotNull
    double volatility;

    @ApiModelProperty(value = "返利率",required = true)
    @NotNull
    RateStructDto returnRate;

    @ApiModelProperty(value = "红利票息率",required = true)
    @NotNull
    RateStructDto bonusRate;

    @ApiModelProperty(value = "敲出观察序列",required = true)
    @NotNull
    List<KnockOutScheduleDto> knockoutSchedules;

    @ApiModelProperty(value = "敲出观察总次数",required = true)
    @NotNull
    int totalObservations;

    @ApiModelProperty(value = "成交时间",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime productStartDate;

    @ApiModelProperty(value = "到期时间",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime productEndDate;

    @ApiModelProperty(value = "成交时标的合约价格",required = true)
    @NotNull
    double entryUnderlyingPrice;

    @ApiModelProperty(value = "敲入障碍",required = true)
    @NotNull
    LevelDto knockinBarrier;

    @ApiModelProperty(value = "是否敲入",required = true)
    @NotNull
    Boolean alreadyKnockedIn;

    @ApiModelProperty(value = "敲入转看跌的执行价格或敲入转熊市价差的高执行价格",required = true)
    @NotNull
    LevelDto strikeOnceKnockedin;

    @ApiModelProperty(value = "敲入转看跌则为0；敲入转熊市价差的低执行价格",required = true)
    @NotNull
    LevelDto strike2OnceKnockedin;

}
