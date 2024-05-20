package org.orient.otc.quote.dto.jni;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 隐含波动率
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class AIBlackImpliedVolRequestDto {
    @ApiModelProperty(value = "期权合约规定的行权方式(值域为{\"european\",  \"american\"})",required = true)
    @NotNull
    private String exerciseType;

    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型",required = true)
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

    @ApiModelProperty(value = "定价时点的时间",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime evaluationTime;

    @ApiModelProperty(value = "定价时点的时间",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime expiryTime;

    /**
     * 无风险利率
     */
    @ApiModelProperty(value = "无风险利率",required = true)
    @NotNull
    private Double riskFreeInterestRate;

    /**
     * 股息率
     */
    @ApiModelProperty(value = "股息率",required = true)
    @NotNull
    private Double dividendYield;

    /**
     * 期权现值
     */
    @ApiModelProperty(value = "期权现值",required = true)
    @NotNull
    private Double pv;
}
