package org.orient.otc.quote.dto.quote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.OptionCombTypeEnum;
import org.orient.otc.quote.enums.OpenOrCloseEnum;
import org.orient.otc.quote.enums.TradeTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 定价计算
 */
@Data
@ApiModel(value = "定价计算")
public class QuoteCalculateDTO {

    /**
     * 单腿还是组合
     */
    @ApiModelProperty(value = "单腿还是组合",required = true)
    @NotNull
    private TradeTypeEnum tradeType;

    /**
     * 组合类型
     */
    @ApiModelProperty(value = "组合类型",required = true)
    private OptionCombTypeEnum optionCombType;

    /**
     * 开仓平仓
     */
    @ApiModelProperty(value = "开仓平仓")
    private OpenOrCloseEnum openOrClose;

    /**
     * 计算列表
     */
    @ApiModelProperty(value = "计算列表",required = true)
    @NotNull
    @Valid
    List<QuoteCalculateDetailDTO> quoteList;
}
