package org.orient.otc.dm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.dm.enums.MainContractEnum;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * BenchMark
 */
@Data
@ApiModel
public class UnderlyingVolatilityDTO {
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
