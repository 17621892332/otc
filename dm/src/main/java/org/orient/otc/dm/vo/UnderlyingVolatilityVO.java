package org.orient.otc.dm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orient.otc.api.dm.enums.MainContractEnum;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * benchmark
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class UnderlyingVolatilityVO {
    @ApiModelProperty(value = "标的资产码")
    @NotNull
    private String underlyingCode;
    @ApiModelProperty(value = "是否是主力合约")
    @NotNull
    private MainContractEnum mainContract;
    @ApiModelProperty(value = "股息率")
    @NotNull
    private BigDecimal dividendYield;
    @ApiModelProperty(value = "品种id")
    @NotNull
    private Integer varietyId;
    @ApiModelProperty(value = "波动率偏移量")
    @NotNull
    private BigDecimal volOffset;
}
