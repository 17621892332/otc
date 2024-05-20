package org.orient.otc.quote.entity;

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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
@ApiModel
public class ExchangeTrade extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    // @TableId(value="instrument_id", type= IdType.AUTO)
    @ApiModelProperty(value = "经纪公司代码")
    private String brokerID;
    @ApiModelProperty(value = "用户代码")
    private String userID;
    @ApiModelProperty(value = "资金账号")
    private String investorID;
    @ApiModelProperty(value = "合约代码")
    private String instrumentID;
    @ApiModelProperty(value = "成交编号")
    private String tradeID;
    /**
     *
     */
    @ApiModelProperty(value = "买卖方向")
    private String direction;
    @ApiModelProperty(value = "交易所代码")
    private String exchangeID;
    @ApiModelProperty(value = "报单引用")
    private String orderRef;
    @ApiModelProperty(value = "交易所报单编号")
    private String orderSysID;
    @ApiModelProperty(value = "成交日期")
    private String tradeDate;
    @ApiModelProperty(value = "成交时间")
    private String tradeTime;
    @ApiModelProperty(value = "成交类型")
    private String tradeType;
    @ApiModelProperty(value = "投机套保标志")
    private String hedgeFlag;
    @ApiModelProperty(value = "成交价格")
    private Double price;
    @ApiModelProperty(value = "成交手数")
    private Integer volume;
    /**
     *  0开仓 非0都是平仓
     */
    @ApiModelProperty(value = "开平标志")
    private String offsetFlag;
    @ApiModelProperty(value = "交易日")
    private String tradingDay;
    @ApiModelProperty(value = "成交时刻标的期货价格")
    private BigDecimal tradingFuturesPrice;
}
