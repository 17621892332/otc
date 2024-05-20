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
public class AccPricerConfirmBookDTO implements Serializable {


    /**
     * 客户ID
     */
    private Integer clientId;

    /**
     * 客户名称
     */
    private String clientName;


    private OptionTypeEnum optionType;

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
     * 产品卖方
     */
    private String productSellName;
    /**
     * 标的合约
     */
    private String underlyingCode;

    private String underlyingCodeByExchange;

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 成交数量String
     */
    private String tradeVolumeString;

    /**
     * 每日数量
     */
    private BigDecimal basicQuantity;

    /**
     * 每日数量String
     */
    private String basicQuantityString;

    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 成交金额String
     */
    private String totalAmountString;
    /**
     * 交易日期
     */
    private LocalDate tradeDate;
    /**
     * 交易日期
     */
    private String tradeDateStr;

    /**
     * 起始观察日期
     */
    private LocalDate startObsDate;
    /**
     * 起始观察日期
     */
    private String startObsDateStr;

    /**
     * 期末观察日
     */
    private LocalDate endObsDate;
    /**
     * 期末观察日
     */
    private String endObsDateStr;

    /**
     * 观察日
     */
    private Integer obsNumber;


    /**
     * 单位
     */
    private String quoteUnit;

    /**
     * 敲出价格
     */
    private BigDecimal barrier;

    /**
     * 敲出价格
     */
    private String barrierString;

    /**
     * 行权价格
     */
    private BigDecimal strike;
    /**
     * 行权价格str
     */
    private String strikeString;
    /**
     * 入场价格
     */
    private BigDecimal entryPrice;

    /**
     * 入场价格str
     */
    private String entryPriceString;

    /**
     * 单位固定赔付
     */
    private BigDecimal fixedPayment;

    /**
     * 单位固定赔付
     */
    private String fixedPaymentString;
    /**
     * 杠杆系数
     */
    private BigDecimal leverage;

    /**
     * 杠杆系数
     */
    private String leverageString;



    /**
     * 敲出赔付
     */
    private BigDecimal knockoutRebate;

    /**
     * 敲出赔付
     */
    private String knockoutRebateString;

    /**
     * 到期倍数
     */
    private BigDecimal expireMultiple;

    /**
     * 到期倍数
     */
    private String expireMultipleString;

    /**
     * 结算方式
     */
    private SettleTypeEnum settleType;

    /**
     * 结算方式
     */
    private String settleTypeString;

    /**
     * 日期
     */
    private String tradeDateEndStr;

}
