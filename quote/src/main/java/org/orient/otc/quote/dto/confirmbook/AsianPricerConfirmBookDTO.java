package org.orient.otc.quote.dto.confirmbook;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.SettleTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 香草交易确认书对象
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AsianPricerConfirmBookDTO implements Serializable {


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
     * 客户方向
     */
    private BuyOrSellEnum buyOrSell;
    /**
     * 产品买方
     */
    private String productBuyName;

    /**
     * 期权类型
     */
    private OptionTypeEnum optionType;
    /**
     * 期权类型
     */
    private String optionTypeName;

    /**
     * 看涨看跌
     */
    private CallOrPutEnum callOrPut;

    /**
     * 看涨看跌
     */
    private String callOrPutName;

    /**
     * 标的合约
     */
    private String underlyingCode;

    private String underlyingCodeByExchange;

    /**
     * 成交日期
     */
    private LocalDate tradeDate;

    /**
     * 成交日期
     */
    private String tradeDateStr;


    /**
     * 到期日期
     */
    private LocalDate maturityDate;


    /**
     * 到期日期
     */
    private String maturityDateStr;


    /**
     * 采价起始日
     */
    private LocalDate startObsDate;
    /**
     * 采价起始日
     */
    private String startObsDateStr;

    /**
     * 采价终止日
     */
    private LocalDate endObsDate;
    /**
     * 采价终止日
     */
    private String endObsDateStr;


    /**
     * 采价日数
     */
    private Integer obsNumber;

    /**
     * 交易数量
     */
    private BigDecimal tradeVolume;

    /**
     * 交易数量String
     */
    private String tradeVolumeString;

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
     * 增强价格
     */
    private BigDecimal enhancedStrike;

    /**
     * 增强价格
     */
    private String enhancedStrikeString;
    /**
     * 期权￥单价
     */
    private BigDecimal optionPremium;
    /**
     * 期权￥单价
     */
    private String optionPremiumString;
    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 成交金额String
     */
    private String totalAmountString;

    /**
     * 名义本金
     */
    private BigDecimal notionalPrincipal;

    /**
     * 名义本金
     */
    private String notionalPrincipalString;

    /**
     * 结算方式
     */
    private SettleTypeEnum settleType;

    /**
     * 结算方式
     */
    private String settleTypeString;

    /**
     * 单位
     */
    private String quoteUnit;


    /**
     * 到期收益结构
     */
    private String yieldStructure;


    /**
     * 日期
     */
    private String tradeDateEndStr;

}
