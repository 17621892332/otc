package org.orient.otc.quote.vo;

import cn.hutool.core.annotation.Alias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("持仓记录导出")
public class PositionExportVo {
    @Alias("簿记账户")
    private String assetUnitName;
    @Alias("交易类型")
    private String TradeType;
    @Alias("标的代码")
    private String underlyingCode;
    @Alias("标的名称")
    private String underlyingName;
    @Alias("期权代码")
    private String optionCode;
    @Alias("昨日持仓")
    private Integer ydPosition;
    @Alias("持仓手数")
    private Integer position;
    @Alias("持仓数量")
    private Integer positionCount;
    @Alias("存续总额")
    private BigDecimal positionCost;
    @Alias(value = "交易方向")
    private String direction;
    @Alias(value = "标的现价")
    private BigDecimal underlyingPrice;
}
