package org.orient.otc.quote.dto.collateral;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.CollateralEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("抵押品-赎回dto")
public class CollateralRedemptionDto {

    @ApiModelProperty(value = "id")
    @NotNull(message = "ID不能为空")
    private Integer id;

}
