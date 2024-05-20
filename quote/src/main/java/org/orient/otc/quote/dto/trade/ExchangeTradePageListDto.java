package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("交易流水dto")
public class ExchangeTradePageListDto extends BasePage {

    /*@ApiModelProperty("持仓日期")
    LocalDate date;*/

    @ApiModelProperty("簿记账户")
    Set<Integer> assetUnitIds;

    @ApiModelProperty("簿记账户组")
    Set<Integer> assetUnitGroupIds;



    @ApiModelProperty("期权代码")
    private String optionCode;

    // 我们只有期权/期货 , 暂时不分
    @ApiModelProperty("交易类型")
    private Integer traderType;

    @ApiModelProperty("标的代码")
    private Set<String> underlyingCodes;

    @ApiModelProperty("买卖方向")
    /**
     * 1:多头开仓
     * 2:多头平仓
     * 3:空头开仓
     * 4:空头平仓
     */
    private String direction;

    @ApiModelProperty("成交日期起始")
    private LocalDate tradeDateStart;
    @ApiModelProperty("成交日期结束")
    private LocalDate tradeDateEnd;
}
