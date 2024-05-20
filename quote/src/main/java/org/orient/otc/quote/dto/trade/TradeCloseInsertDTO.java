package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.quote.enums.TradeCloseTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 录入平仓
 */
@ApiModel("录入平仓")
@Data
public class TradeCloseInsertDTO {

    /**
     * 平仓列表
     */
    @ApiModelProperty(value = "平仓列表",required = true)
    @NotNull(message = "平仓列表不能为空")
    @Valid
    private List<TradeCloseDTO> tradeCloseDTOList;

    /**
     * 平仓类型
     */
    @ApiModelProperty(value = "平仓类型",required = true)
    @NotNull(message = "平仓类型不能为空")
    private TradeCloseTypeEnum tradeCloseType;
}
