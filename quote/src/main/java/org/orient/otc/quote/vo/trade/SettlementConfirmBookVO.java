package org.orient.otc.quote.vo.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.OptionCombTypeEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 结算确认书列表对象
 * @author dzrh
 */
@Data
public class SettlementConfirmBookVO {

    private Integer id;
    /**
     * 结算确认书文件路径
     */
    @ApiModelProperty(value = "结算确认书文件路径")
    private String settlementFilePath;

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
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;

    /**
     * 交易确认书编号
     */
    @ApiModelProperty(value = "交易确认书编号")
    private String tradeConfirmCode;
    /**
     * 结算确认书编号
     */
    @ApiModelProperty(value = "结算确认书编号")
    private String settlementConfirmCode;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
     * 平仓数量
     */
    @ApiModelProperty(value = "平仓数量")
    private BigDecimal closeVolume;

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
     * 平仓金额
     */
    @ApiModelProperty(value = "平仓金额")
    private BigDecimal closeTotalAmount;

    /**
     * 平仓盈亏
     */
    @ApiModelProperty(value = "平仓盈亏")
    private BigDecimal profitLoss;

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
     * 平仓日期
     */
    @ApiModelProperty(value = "平仓日期")
    private LocalDate closeDate;

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
     * 簿记ID
     */
    @ApiModelProperty(value = "簿记ID")
    private Integer assetId;

    /**
     * 簿记名称
     */
    @ApiModelProperty(value = "簿记账户")
    private String assetName;

    private Integer traderId;

    @ApiModelProperty(value = "交易员")
    private String traderName;


    private Integer tradeAddId;

    @ApiModelProperty(value = "交易添加人")
    private String tradeAddName;

    @ApiModelProperty(value = "交易添加时间")
    private LocalDateTime tradeAddTime;

    /**
     * 操作人ID
     */
    @ApiModelProperty(value = "操作人ID")
    private Integer creatorId;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private String creatorName;

    /**
     * 记录创建时间
     */
    @ApiModelProperty(value = "操作时间")
    private LocalDateTime createTime;

}
