package org.orient.otc.quote.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ExchangeTradeVo implements Serializable {
    @ApiModelProperty(value = "簿记账户")
    private String assetUnitName;
    @ApiModelProperty(value = "交易类型")
    private String tradeType;
    @ApiModelProperty(value = "成交日期")
    private String tradetingDay;
    @ApiModelProperty(value = "交易方向")
    private String direction;
    @ApiModelProperty(value = "标的代码")
    private String underlyingCode;
    @ApiModelProperty("标的名称")
    private String underlyingName;
    @ApiModelProperty("交易手数")
    private Integer volume;
    @ApiModelProperty("交易数量")
    private Integer volumeCount;
    @ApiModelProperty("成交价")
    private Double price;
    @ApiModelProperty("操作时间")
    private String operationTime;
    @ApiModelProperty("期权代码")
    private String instrumentId;
}
