package org.orient.otc.quote.vo.daily;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品持仓日报
 * @author dzrh
 */
@Data
public class CommodityPositionDailyVO {
    /**
     * 交易对手方名称
     */
    private String clientName;

    /**
     * 交易对手方的统一社会信用代码
     */
    private String clientLicenseCode;

    /**
     * 交易对手方2的LEI
     */
    private String clientLei;

    /**
     * 主协议类型
     */
    private String protocolType;

    /**
     * 主协议日期
     */
    private LocalDate protocolDate;

    /**
     * 交易确认书编号
     */
    private String tradeConfirmCode;
    /**
     * 交易确认书编号
     */
    private String tradeCode;

    /**
     * 多腿编号
     */
    private Integer confirmId;

    /**
     * 交易确认时间
     */
    private LocalDate confirmDate;

    /**
     * 生效日期
     */
    private LocalDate tradeDate;

    /**
     * 到期日
     */
    private LocalDate maturityDate;

    //产品及标的基本信息start
    /**
     * 填报方方向 东证润和买卖方向: 东证润和买为B 东证润和卖为S
     */
    private String buyOrSell;

    /**
     * 资产类型 1. 标的资产以 .SH结尾的，以IF、IC、IM、IH、IO、MO、HO开头的为权益类 2. TS、TF、T、TL开头的为利率类 3. 若出现上述提及情况以外的，进行提示，后续进行增添
     */
    private String assetType;

    /**
     * 工具类型 SW:掉期 OP:期权 FW:远期 O:其他 1. 结构类型为香草期权、亚式期权、累计期权、雪球的为期权类 2. 结构类型为远期的则为远期 3. 若出现上述提及情况以外的，进行提示，后续进行增添
     */
    private String toolType;

    /**
     * 标的资产所属板块
     */
    private String underlyingPlate;

    /**
     * 标的资产品种 参见标的品种代码表
     */
    private String varietyCode;

    /**
     * 标的资产对应合约
     */
    private String exchangeUnderlyingCode;

    /**
     * 标的期初价格
     */
    private BigDecimal entryPrice;

    /**
     * 标的期初价格货币
     */
    private String underlyingInitialPriceCurrency;

    /**
     * 标的期初价格符号
     */
    private String underlyingInitialPriceSymbol;

    //期权产品基础信息

    /**
     * 期权行权时间类型 当工具类型为期权时，填写此项: 1. 欧式期权: EU 2. 美式期权: US 3. 百慕大期权: BD 4. 其他类型为: O 若工具类型不为期权则不填
     */
    private String exerciseType;

    /**
     * 期权行权时间类型 1: 看跌, 2: 看涨, 3: 选择期权, 9: 其他 当期权类型为雪球或者凤凰时，填写敲入状态时香草期权的期权权利类型（如敲入转看跌雪球填1） 1. 看涨雪球及其他看跌期权选1 2.
     * 看跌雪球及其他看涨期权选2 3. 二元小雪球选9 4. 若出现上述提及情况以外的，进行提示，后续进行增添
     */
    private String optionRightsType;

    /**
     * 执行价格
     */
    private BigDecimal strike;

    /**
     * 执行价格2
     */
    private BigDecimal strike2;


    /**
     * 执行价格3
     */
    private BigDecimal strike3;

    /**
     * 执行价格货币
     */
    private BigDecimal strikeCurrency;

    /**
     * 执行价格符号
     */
    private BigDecimal strikeSymbol;

    /**
     * 观察类型
     */
    private String observeType;

    /**
     * 观察起始日
     */
    private String observeStartDateString;
    /**
     * 观察结束日
     */
    private String observeEndDateString;

    /**
     * 观察周期
     */
    private String observationPeriod;

    /**
     * 观察周期乘数
     */
    private String observePeriodMultiplierString;
    /**
     * 收益计算方式
     */
    private String revenueCalculationMethod;

    /**
     * 保底收益
     */
    private String guaranteedIncomeString;

    /**
     * 保底收益货币
     */
    private String guaranteedIncomeCurrency;

    /**
     * 保底收益符号
     */
    private String guaranteedIncomeSymbol;

    /**
     * 是否已敲入
     */
    private String knockIn;

    //亚式期权
    /**
     * 估值时已观察均价
     */
    private String observeAverageString;

    //二元期权、区间累计期权相关字段
    /**
     * 行权收益1
     */
    private String exerciseBenefitsString;

    /**
     * 行权收益2
     */
    private String exerciseBenefits2String;

    /**
     * 行权收益3
     */
    private String exerciseBenefits3String;

    /**
     * 行权收益货币
     */
    private String exerciseBenefitsCurrency;

    /**
     * 行权收益符号
     */
    private String exerciseBenefitsSymbol;

    //单鲨、双鲨、单障碍敲入期权相关字段
    /**
     * 障碍价1
     */
    private String barriersPriceString;

    /**
     * 障碍价1触碰方向
     */
    private String barriersPriceTouchDirection;

    /**
     * 障碍价1类型
     */
    private String barriersPriceType;

    /**
     * 障碍价2
     */
    private String barriersPrice2String;
    /**
     * 障碍价2触碰方向
     */
    private String barriersPrice2TouchDirection;
    /**
     * 障碍价2类型
     */
    private String barriersPrice2Type;
    /**
     * 补偿收益1
     */
    private String compensationIncomeString;
    /**
     * 补偿收益2
     */
    private String compensationIncome2String;
    /**
     * 补偿收益货币
     */
    private String compensationIncomeCurrency;
    /**
     * 补偿收益符号
     */
    private String compensationIncomeSymbol;

    //雪球、凤凰期权相关字段
    /**
     * SA&PA-敲入障碍价
     */
    private String knockInBarrierPriceString;
    /**
     * SA&PA-敲入观察期
     */
    private String knockInObservationPeriod;

    /**
     * SA&PA-敲出障碍价
     */
    private String knockOutBarrierPriceString;
    /**
     * SA&PA-Coupon障碍价
     */
    private String couponBarrierPriceString;
    /**
     * SA&PA-Coupon年化收益率
     */
    private String couponAnnualRateString;

    //远期产品
    /**
     * 远期价格
     */
    private String forwardPriceString;
    /**
     * 远期价格货币
     */
    private String forwardPriceCurrency;
    /**
     * 远期价格符号
     */
    private String forwardPriceSymbol;

    //互换产品
    /**
     * 固定收益
     */
    private String fixedIncomeString;
    /**
     * 固定收益货币
     */
    private String fixedIncomeCurrency;
    /**
     * 固定收益符号
     */
    private String fixedIncomeSymbol;
    /**
     * 浮动方方向
     */
    private String floatingDirection;
    /**
     * 支付周期
     */
    private String paymentCycle;

    /**
     * 支付周期乘数
     */
    private String paymentCycleMultiplierString;

    //名义金额及名义数量
    /**
     * 初始名义金额
     */
    private String initialNominalAmount;

    /**
     * 已平仓总名义金额
     */
    private String totalClosedPositionsAmount;

    /**
     * 外汇兑人民币期初汇率
     */
    private String foreignCurrencyRate;

    /**
     * 初始名义数量
     */
    private String initialNominalNumber;

    /**
     * 已平仓总名义数量
     */
    private String totalClosedPositionsNumber;

    /**
     * 数量单位
     */
    private String quoteUnitEn;

    /**
     * 是否为年化名义金额
     */
    private String isAnnualizedNominalAmount;

    /**
     * 参与率
     */
    private String participateRateString;


    //估值信息
    /**
     * 合约估值时标的价格
     */
    private String contractValuationPrice;

    /**
     * 合约价值
     */
    private String contractPriceString;

    /**
     * 估值方法
     */
    private String valuationMethod;

    /**
     * 估值波动率
     */
    private String valuationVolatility;
    /**
     * 一年有效天数
     */
    private String validDays;
    private String delta;
    private String gamma;
    private String vega;
    private String theta;
    private String rho;
    private String deltaCash;
    private String gammaCash;
    /**
     * 无风险利率
     */
    private String riskFreeRateString;

    /**
     * 股息
     */
    private String dividendString;


    //结算信息
    /**
     * 结算方式
     */
    private String settlementMethod;
    /**
     * 最后结算日
     */
    private String lastUnWindDateString;


}
