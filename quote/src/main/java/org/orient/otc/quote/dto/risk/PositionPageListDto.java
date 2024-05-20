package org.orient.otc.quote.dto.risk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("交易dto")
public class PositionPageListDto extends BasePage {

    @ApiModelProperty("持仓日期")
    LocalDate date;

    @ApiModelProperty("簿记账户")
    Set<Integer> assetUnitIds;

    @ApiModelProperty("簿记账户组")
    Set<Integer> assetUnitGroupIds;

    @ApiModelProperty("标的代码")
    private Set<String> underlyingCodes;

    @ApiModelProperty("期权代码")
    private String optionCode;

    // 我们只有期权/期货
    @ApiModelProperty("交易类型")
    private Integer traderType;

    @ApiModelProperty("买卖方向")
    private String direction;
}
