package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.orient.otc.api.quote.enums.SettleTypeEnum;

@Getter
@Setter
public class UpdateTradeSettleTypeDTO {

    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;
    /**
     * 结算方式
     */
    @ApiModelProperty(value = "结算方式")
    private SettleTypeEnum settleType;

}
