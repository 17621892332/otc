package org.orient.otc.quote.vo;

import cn.hutool.core.annotation.Alias;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ExchangeTradeExportVo implements Serializable {
    @Alias(value = "簿记账户")
    private String assetUnitName;
    @Alias(value = "交易类型")
    private String tradeType;
    @Alias(value = "成交日期")
    private String tradetingDay;
    @Alias(value = "交易方向")
    private String direction;
    @Alias(value = "标的代码")
    private String underlyingCode;
    @Alias("标的名称")
    private String underlyingName;
    @Alias("期权代码")
    private String instrumentId;
    @Alias("交易手数")
    private Integer volume;
    @Alias("交易数量")
    private Integer volumeCount;
    @Alias("成交价")
    private Double price;
    @Alias("操作时间")
    private String operationTime;
}
