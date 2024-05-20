package org.orient.otc.quote.vo.volatility;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.quote.dto.volatility.VolatityDataDto;
import org.orient.otc.quote.dto.volatility.VolatityDeltaDataDto;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel
public class DeltaVolToStrikeVolVo {
    @ApiModelProperty(value = "bid波动率",required = true)
    @NotEmpty
    private List<VolatityDataDto> bidData;
    @ApiModelProperty(value = "mid波动率",required = true)
    @NotEmpty
    private List<VolatityDataDto> midData;
    @ApiModelProperty(value = "ask波动率",required = true)
    @NotEmpty
    private List<VolatityDataDto> askData;

}
