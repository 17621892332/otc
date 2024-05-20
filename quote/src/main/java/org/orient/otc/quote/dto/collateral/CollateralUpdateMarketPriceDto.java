package org.orient.otc.quote.dto.collateral;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("抵押品-修改盯市价格dto")
public class CollateralUpdateMarketPriceDto {

    @ApiModelProperty(value = "id")
    @NotNull(message = "ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "盯市价格")
    @NotNull(message = "盯市价格不能为空")
    private BigDecimal markPrice;

}
