package org.orient.otc.quote.dto.capitalrecords;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CapitalRecordsDeleteDto{
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Integer id;

    private Boolean isTransDetail;
}
