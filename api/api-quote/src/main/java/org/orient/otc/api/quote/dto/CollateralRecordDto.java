package org.orient.otc.api.quote.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CollateralRecordDto{

    @ApiModelProperty("抵押时间-开始")
    private LocalDate startCollateralDate;

    @ApiModelProperty("抵押时间-结束")
    private LocalDate endCollateralDate;

    @ApiModelProperty(value = "客户id")
    private Integer clientId;

}
