package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("远期分页查询dto")
public class TradeDetailPageListDto extends BasePage {

    @ApiModelProperty(value = "观察日期")
    private LocalDate obsDate;

    @ApiModelProperty(value = "交易编号")
    private String tradeCode;

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(value = "期权类型")
    private List<OptionTypeEnum> optionTypeList;

    @ApiModelProperty(value = "标的合约")
    private String underlyingCode;
}
