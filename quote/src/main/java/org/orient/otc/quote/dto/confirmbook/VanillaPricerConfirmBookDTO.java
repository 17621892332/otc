package org.orient.otc.quote.dto.confirmbook;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 香草交易确认书对象
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VanillaPricerConfirmBookDTO implements Serializable {


    /**
     * 期权类型
     */
    private OptionTypeEnum optionType;
    /**
     * 期权类型
     */
    private String optionTypeName;
    /**
     * 客户ID
     */
    private Integer clientId;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 交易编号
     */
    private String tradeCode;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;
    /**
     * 交易日期
     */
    private String tradeDateStr;

    /**
     * 日期
     */
    private String tradeDateEndStr;

    /**
     * 交易到期日
     */
    private LocalDate maturityDate;
    /**
     * 交易到期日
     */
    private String maturityDateStr;
    /**
     * 标的合约
     */
    private String underlyingCode;

    private String underlyingCodeByExchange;

    /**
     * 客户方向
     */
    private BuyOrSellEnum buyOrSell;
    /**
     * 润和方向
     */
    private String dzBuyOrSellName;

    /**
     * 行权方式
     */
    private ExerciseTypeEnum exerciseType;
    /**
     * 行权方式
     */
    private String exerciseTypeName;
    /**
     * 看涨看跌
     */
    private CallOrPutEnum callOrPut;

    /**
     * 看涨看跌
     */
    private String callOrPutName;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 成交数量String
     */
    private String tradeVolumeString;

    /**
     * 单位
     */
    private String quoteUnit;

    /**
     * 入场价格
     */
    private BigDecimal entryPrice;

    /**
     * 入场价格str
     */
    private String entryPriceString;

    /**
     * 行权价格
     */
    private BigDecimal strike;
    /**
     * 行权价格str
     */
    private String strikeString;

    /**
     * 权利金单价
     */
    private BigDecimal optionPremium;

    /**
     * 期权单价String
     */
    private String optionPremiumString;
    /**
     * 名义本金
     */
    private BigDecimal notionalPrincipal;
    /**
     * 名义本金String
     */
    private String notionalPrincipalString;

    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 成交金额String
     */
    private String totalAmountString;

}
