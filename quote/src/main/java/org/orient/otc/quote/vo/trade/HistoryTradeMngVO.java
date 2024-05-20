package org.orient.otc.quote.vo.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.TradeObsDateVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class HistoryTradeMngVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;
    /**
     * 关联交易编号
     */
    @ApiModelProperty(value = "关联交易编号")
    private String relevanceTradeCode;
    /**
     * 标的合约
     */
    @ApiModelProperty(value = "标的合约")
    private String underlyingCode;

    /**
     * 标的名称
     */
    @ApiModelProperty(value = "标的名称")
    private String underlyingName;
    /**
     * 交易方向
     */
    @ApiModelProperty(value = "交易方向")
    private BuyOrSellEnum buyOrSell;
    /**
     * 交易方向
     */
    @ApiModelProperty(value = "交易方向")
    private String buyOrSellName;

    /**
     * 交易日期
     */
    @ApiModelProperty(value = "交易日期")
    private LocalDate tradeDate;


    /**
     * 到期日期
     */
    @ApiModelProperty(value = "到期日期")
    private LocalDate maturityDate;
    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型")
    private OptionTypeEnum optionType;
    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型")
    private String optionTypeName;
    /**
     * 组合类型
     */
    @ApiModelProperty(value = "组合类型")
    private OptionCombTypeEnum optionCombType;
    /**
     * 组合类型
     */
    @ApiModelProperty(value = "组合类型")
    private String optionCombTypeName;

    /**
     * 组合类型
     */
    @ApiModelProperty(value = "结算方式")
    private SettleTypeEnum settleType;
    /**
     * 结算方式
     */
    @ApiModelProperty(value = "结算方式")
    private String settleTypeName;
    /**
     *  看涨看跌
     */
    @ApiModelProperty(value = "看涨看跌")
    private CallOrPutEnum callOrPut;
    /**
     *  看涨看跌
     */
    @ApiModelProperty(value = "看涨看跌")
    private String callOrPutName;
    /**
     * 采价次数
     */
    @ApiModelProperty(value = "采价次数")
    private Integer obsNumber;

    /**
     * 敲出价格
     */
    @ApiModelProperty(value = "敲出价格")
    private BigDecimal barrier;
    /**
     * 单日数量
     */
    @ApiModelProperty(value = "单日数量")
    private BigDecimal basicQuantity;
    /**
     * 杠杆系数
     */
    @ApiModelProperty(value = "杠杆系数")
    private BigDecimal leverage;

    /**
     * 固定赔付
     */
    private BigDecimal fixedPayment;

    /**
     * 敲出赔付
     */
    @ApiModelProperty(value = "敲出赔付")
    private BigDecimal knockoutRebate;

    /**
     * 到期倍数
     */
    @ApiModelProperty(value = "敲出赔付")
    private BigDecimal expireMultiple;
    /**
     * 入场价格
     */
    @ApiModelProperty(value = "入场价格")
    private BigDecimal entryPrice;

    /**
     * 行权价格
     */
    @ApiModelProperty(value = "行权价格")
    private BigDecimal strike;

    /**
     * 成交单价
     */
    @ApiModelProperty(value = "成交单价")
    private BigDecimal optionPremium;

    /**
     * 成交数量
     */
    @ApiModelProperty(value = "成交数量")
    private BigDecimal tradeVolume;

    /**
     * 成交总额
     */
    @ApiModelProperty(value = "成交总额")
    private BigDecimal totalAmount;

    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    private BigDecimal notionalPrincipal;
    /**
     * 成交波动率
     */
    @ApiModelProperty(value = "成交波动率")
    private BigDecimal tradeVol;
    /**
     * 观察日列表
     */
    private List<TradeObsDateVO> obsDateList;
    /**
     * 是否敲入
     */
    private Boolean alreadyKnockedIn;
    /**
     * 是否敲入
     */
    private String alreadyKnockedInFormat;
    /**
     * 红利票息
     */
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    private Boolean bonusRateAnnulized;

    private BigDecimal bonusRateStructValueFormat;

    @ApiModelProperty(value = "敲入价格")
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean knockinBarrierRelative;

    @ApiModelProperty(value = "敲入价格")
    private String knockinBarrierValueFormat;

    /**
     * 敲入行权价格是否为相对水平值
     */
    private Boolean strikeOnceKnockedinRelative;

    @ApiModelProperty(value = "敲入行权价格1")
    private BigDecimal strikeOnceKnockedinValue;

    @ApiModelProperty(value = "敲入行权价格1")
    private String strikeOnceKnockedinValueFormat;

    /**
     * 敲入行权价格2是否为相对水平值
     */
    private Boolean strike2OnceKnockedinRelative;

    @ApiModelProperty(value = "敲入行权价格2")
    private BigDecimal strike2OnceKnockedinValue;

    @ApiModelProperty(value = "敲入行权价格2")
    private String strike2OnceKnockedinValueFormat;

    /**
     * 敲出价格格式化
     */
    @ApiModelProperty(value = "敲出价格")
    private String barrierValueFormat;

    /**
     * 敲出观察日
     */
    @ApiModelProperty(value = "敲出观察日")
    private String barrierObsDateFormat;

    /**
     * 敲出票息
     */
    @ApiModelProperty(value = "敲出票息")
    private String rebateRateFormat;
    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    private TradeStateEnum tradeState;

    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    private String tradeStateName;
    /**
     * 平仓日期
     */
    @ApiModelProperty(value = "平仓日期")
    private LocalDate closeDate;

    /**
     * 平仓标的价格
     */
    @ApiModelProperty(value = "平仓标的价格")
    private BigDecimal closeEntryPrice;
    /**
     * 平仓价格
     */
    @ApiModelProperty(value = "平仓价格")
    private BigDecimal closePrice;
    /**
     * 平仓波动率
     */
    @ApiModelProperty(value = "平仓波动率")
    private BigDecimal closeVol;

    /**
     * 平仓数量
     */
    @ApiModelProperty(value = "平仓数量")
    private BigDecimal closeVolume;

    /**
     * 平仓金额
     */
    @ApiModelProperty(value = "平仓金额")
    private BigDecimal closeTotalAmount;

    /**
     * 平仓盈亏
     */
    @ApiModelProperty(value = "平仓盈亏")
    private BigDecimal profitLoss;

}
