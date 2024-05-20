package org.orient.otc.quote.dto.risk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRealTimePos {

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

    private Integer assetId;
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
     * 期权类型
     */
    private int optionsType;

    private BigDecimal strikePrice;

    private String expireDate;

    /**
     * 合约代码
     */
    private String underlyingCode;

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
     * 开平交易成本
     */
    @ApiModelProperty(value = "开平交易成本")
    private BigDecimal tradeCost;

    /**
     * 开平总盈亏
     */
    @ApiModelProperty(value = "开平总盈亏")
    private Double totalPnl;

    private BigDecimal day1PnL;

    public ExchangeRealTimePos() {
    }


    public ExchangeRealTimePos(String brokerId, String investorId, String exchangeId, String posiDirection) {
        this.brokerID = brokerId;
        this.investorID = investorId;
        this.exchangeID = exchangeId;
        this.posiDirection = posiDirection;
        this.hedgeFlag = "1";
        this.ydPosition = 0;
        this.position = 0;
        this.openAmount = 0.0;
        this.openVolume = 0;
        this.closeAmount = 0.0;
        this.closeVolume = 0;
        this.preSettlementPrice = 0.0;
        this.closeProfit = 0.0;
        this.positionProfit = 0.0;
        this.useMargin = 0.0;
        this.commission = 0.0;
        this.openCost = 0.0;
        this.tradeCost = BigDecimal.ZERO;
        this.totalPnl = 0.0;
    }
}
