package org.orient.otc.quote.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionPageListVo {
    // 方便分页过滤
    @ApiModelProperty("簿记账户ID")
    private Integer assetUnitId;

    @ApiModelProperty("簿记账户名称")
    private String assetUnitName;

    // 方便分页过滤
    @ApiModelProperty("簿记账户组ID")
    private Integer assetUnitGroupId;

    @ApiModelProperty("交易类型")
    private String tradeType;

    @ApiModelProperty("标的代码")
    private String underlyingCode;

    @ApiModelProperty("标的名称")
    private String underlyingName;

    @ApiModelProperty("期权代码")
    private String optionCode;

    @ApiModelProperty("持仓手数")
    private Integer position;

    @ApiModelProperty("昨日持仓手数")
    private Integer ydPosition;

    @ApiModelProperty("持仓数量")
    private Integer positionCount;

    @ApiModelProperty(value = "交易方向")
    private String direction;

    @ApiModelProperty(value = "标的现价")
    private BigDecimal underlyingPrice;

}
