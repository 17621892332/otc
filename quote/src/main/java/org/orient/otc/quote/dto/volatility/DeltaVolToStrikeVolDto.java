package org.orient.otc.quote.dto.volatility;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel
public class DeltaVolToStrikeVolDto {
    @ApiModelProperty(value = "bid的delta波动率",required = true)
    @NotEmpty
    private List<VolatityDeltaDataDto> bidDeltaData;
    @ApiModelProperty(value = "mid的delta波动率",required = true)
    @NotEmpty
    private List<VolatityDeltaDataDto> midDeltaData;
    @ApiModelProperty(value = "ask的delta波动率",required = true)
    @NotEmpty
    private List<VolatityDeltaDataDto> askDeltaData;

}
