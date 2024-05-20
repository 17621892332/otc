package org.orient.otc.quote.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper=false)
public class SnowKnockedinLogPageDto extends BasePage {
    @ApiModelProperty("敲入日期")
    private LocalDate knockedInDate;
    @ApiModelProperty("合约代码")
    private String underlyingCode;
    @ApiModelProperty("交易代码")
    private String tradeCode;
    @ApiModelProperty("期权类型")
    private OptionTypeEnum optionType;
    /*@ApiModelProperty("备注")
    private String reamrks;*/
    @ApiModelProperty("敲入价格-开始")
    private BigDecimal knockinBarrierValueStart;
    @ApiModelProperty("敲入价格-结束")
    private BigDecimal knockinBarrierValueEnd;
    @ApiModelProperty("收盘价格-开始")
    private BigDecimal closePriceStart;
    @ApiModelProperty("收盘价格-结束")
    private BigDecimal closePriceEnd;
}
