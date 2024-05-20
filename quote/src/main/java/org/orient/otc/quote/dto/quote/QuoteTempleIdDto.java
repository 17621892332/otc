package org.orient.otc.quote.dto.quote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class QuoteTempleIdDto {
    @ApiModelProperty(value = "模板id",required = true)
    @NotNull
    private Integer templateId;
}
