package org.orient.otc.quote.dto.collateral;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("抵押品-获取盯市价格dto")
public class CollateralGetMarketPriceDto {

    @ApiModelProperty(value = "抵押品名称")
    @NotNull(message = "抵押品不能为空")
    private Integer varietyId;

}
