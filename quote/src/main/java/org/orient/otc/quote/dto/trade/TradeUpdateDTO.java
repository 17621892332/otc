package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dzrh
 */
@ApiModel("录入交易")
@Data
public class TradeUpdateDTO {

    /**
     * 交易方向: 0 客户 1东证
     */
    @ApiModelProperty(value = "交易方向: 0 客户 1东证")
    private Integer tradeDirection;

    @ApiModelProperty(value = "交易列表",required = true)
    @NotNull(message = "交易列表不能为空")
    @Valid
    private List<TradeMngDTO> tradeList;
}
