package org.orient.otc.quote.vo.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.OptionCombTypeEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易确认书下载vo
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TradeConfirmBookVO implements Serializable {

    /**
     * 交易ID
     */
    private Integer id;
    /**
     * 交易确认书文件路径
     */
    @ApiModelProperty(value = "交易确认书文件路径")
    private String tradeConfirmFilePath;

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
     * 交易确认书编号
     */
    @ApiModelProperty(value = "合约编号")
    private String contractCode;
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
     * 成交数量
     */
    @ApiModelProperty(value = "成交数量")
    private BigDecimal tradeVolume;


    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    private BigDecimal notionalPrincipal;


    /**
     * 成交日期
     */
    @ApiModelProperty(value = "成交日期")
    private LocalDate tradeDate;

    /**
     * 标的合约
     */
    @ApiModelProperty(value = "标的合约")
    private String underlyingCode;

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
     * 到期日期
     */
    @ApiModelProperty(value = "到期日期")
    private LocalDate maturityDate;

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

    /**
     * 产品开始日期
     */
    @ApiModelProperty(value = "产品开始日期")
    private LocalDate productStartDate;

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
