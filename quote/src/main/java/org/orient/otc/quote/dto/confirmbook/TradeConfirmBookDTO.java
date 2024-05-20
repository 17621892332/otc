package org.orient.otc.quote.dto.confirmbook;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.common.core.util.FieldAlias;
import org.orient.otc.quote.entity.TradeObsDate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易确认书格式汇总对象
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class TradeConfirmBookDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

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
     * 产品卖方
     */
    private String productSellName;
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
     * 标的合约
     */
    private String underlyingCode;
    /**
     * 标的资产交易场所
     */
    private String  exchange;

    /**
     * 资产类型
     */
    private String assetTyp;

    private String underlyingCodeByExchange;

    /**
     * 成交日期
     */
    private LocalDate tradeDate;

    /**
     * 产品开始日期
     */
    private LocalDate productStartDate;
    /**
     * 到期日期
     */
    private LocalDate maturityDate;

    private List<TradeObsDate> obsDateList;

    /**
     * 采价起始日
     */
    private LocalDate startObsDate;

    /**
     * 采价终止日
     */
    private LocalDate endObsDate;

    /**
     * 采价日数
     */
    private Integer obsNumber;

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 每日数量
     */
    private BigDecimal basicQuantity;

    /**
     * 杠杆系数
     */
    private BigDecimal leverage;

    /**
     * 单位固定赔付
     */
    private BigDecimal fixedPayment;
    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 入场价格
     */
    private BigDecimal entryPrice;


    /**
     * 行权价格
     */
    private BigDecimal strike;

    /**
     * 增强价格
     */
    private BigDecimal enhancedStrike;

    /**
     * 敲出价格
     */
    private BigDecimal barrier;

    /**
     * 敲出赔付
     */
    private BigDecimal knockoutRebate;

    /**
     * 到期倍数
     */
    private BigDecimal expireMultiple;

    /**
     * 期权￥单价
     */
    private BigDecimal optionPremium;

    /**
     * 名义本金
     */
    private BigDecimal notionalPrincipal;

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

}
