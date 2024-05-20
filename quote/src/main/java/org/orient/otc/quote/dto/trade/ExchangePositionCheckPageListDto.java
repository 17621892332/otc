package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("场内持仓校对dto")
public class ExchangePositionCheckPageListDto extends BasePage {

    @ApiModelProperty("场内账号")
    private List<String> investorIds;

    @ApiModelProperty("合约代码")
    private String instrumentId;

    @ApiModelProperty("交易日")
    private LocalDate tradingDay;

    @ApiModelProperty("买卖方向")
    private Integer posiDirection;

    @ApiModelProperty("状态")
    private String status;
}
