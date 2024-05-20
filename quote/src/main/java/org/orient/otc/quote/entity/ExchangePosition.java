package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class ExchangePosition extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 经纪公司代码
     */
    @ApiModelProperty(value = "经纪公司代码")
    private String brokerID;
    /**
     * 用户代码
     */
    @ApiModelProperty(value = "用户代码")
    private String investorID;
    /**
     * 交易所代码
     */
    @ApiModelProperty(value = "交易所代码")
    private String exchangeID;

    /**
     * 合约代码
     */
    @ApiModelProperty(value = "合约代码")
    private String instrumentID;

    /**
     * 持仓多空方向
     */
    @ApiModelProperty(value = "持仓多空方向")
    private String posiDirection;

    /**
     * 投机套保标志
     */
    @ApiModelProperty(value = "投机套保标志")
    private String hedgeFlag;

    /**
     * 上日持仓
     */
    @ApiModelProperty(value = "上日持仓")
    private Integer ydPosition;

    /**
     * 今日总持仓
     */
    @ApiModelProperty(value = "今日总持仓")
    private Integer position;

    /**
     * 开仓量
     */
    @ApiModelProperty(value = "开仓量")
    private Integer openVolume;
    /**
     * 平仓量
     */
    @ApiModelProperty(value = "平仓量")
    private Integer closeVolume;

    /**
     * 开仓金额
     */
    @ApiModelProperty(value = "开仓金额")
    private Double openAmount;

    /**
     * 平仓金额
     */
    @ApiModelProperty(value = "平仓金额")
    private Double closeAmount;

    /**
     * 上次结算价
     */
    @ApiModelProperty(value = "上次结算价")
    private Double preSettlementPrice;

    /**
     * 本次结算价
     */
    @ApiModelProperty(value = "本次结算价")
    private Double settlementPrice;

    /**
     * 平仓盈亏
     */
    @ApiModelProperty(value = "平仓盈亏")
    private Double closeProfit;

    /**
     * 持仓盈亏
     */
    @ApiModelProperty(value = "持仓盈亏")
    private Double positionProfit;

    /**
     * 占用的保证金
     */
    @ApiModelProperty(value = "占用的保证金")
    private Double useMargin;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private Double commission;

    /**
     * 开仓成本
     */
    @ApiModelProperty(value = "开仓成本")
    private Double openCost;
    /**
     * 交易日
     */
    @ApiModelProperty(value = "交易日")
    private String tradingDay;

}
