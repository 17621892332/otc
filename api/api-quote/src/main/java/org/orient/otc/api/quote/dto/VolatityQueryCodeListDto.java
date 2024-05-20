package org.orient.otc.api.quote.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@ApiModel
public class VolatityQueryCodeListDto implements Serializable {

    /**
     * 报价日期
     */
    @ApiModelProperty(value = "报价日期")
    private LocalDate quotationDate;
}
