package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.quote.enums.OpenOrCloseEnum;

import javax.validation.constraints.NotNull;

@Data
public class TradeMsgQueryDto {

    /**
     * 交易ID
     */
    @ApiModelProperty(value = "交易ID",required = true)
    @NotNull(message = "交易ID不能为空")
    private String tradeId;

    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型",required = true)
    @NotNull(message = "交易类型不能为空")
    private OpenOrCloseEnum tradeType;
}
