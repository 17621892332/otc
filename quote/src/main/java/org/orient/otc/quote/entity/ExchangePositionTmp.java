package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
@ApiModel
public class ExchangePositionTmp extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    // @TableId(value="instrument_id", type= IdType.AUTO)
    @ApiModelProperty(value = "经纪公司代码")
    private String brokerID;
    @ApiModelProperty(value = "用户代码")
    private String investorID;
    @ApiModelProperty(value = "交易所代码")
    private String exchangeID;
    @ApiModelProperty(value = "合约代码")
    private String instrumentID;
    @ApiModelProperty(value = "持仓多空方向")
    private String posiDirection;
    @ApiModelProperty(value = "投机套保标志")
    private String hedgeFlag;
    @ApiModelProperty(value = "上日持仓")
    private Integer ydPosition;
    @ApiModelProperty(value = "今日总持仓")
    private Integer position;
    @ApiModelProperty(value = "开仓量")
    private Integer openVolume;
    @ApiModelProperty(value = "平仓量")
    private Integer closeVolume;
    @ApiModelProperty(value = "开仓金额")
    private Double openAmount;
    @ApiModelProperty(value = "平仓金额")
    private Double closeAmount;
    @ApiModelProperty(value = "上次结算价")
    private Double preSettlementPrice;
    @ApiModelProperty(value = "本次结算价")
    private Double settlementPrice;
    @ApiModelProperty(value = "平仓盈亏")
    private Double closeProfit;
    @ApiModelProperty(value = "持仓盈亏")
    private Double positionProfit;
    @ApiModelProperty(value = "占用的保证金")
    private Double useMargin;
    @ApiModelProperty(value = "手续费")
    private Double commission;
    @ApiModelProperty(value = "开仓成本")
    private Double openCost;
    @ApiModelProperty(value = "交易日")
    private String tradingDay;
}
