package org.orient.otc.quote.vo.daily;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 权益持仓日报
 * @author dzrh
 */
@Data
public class EquityPositionDailyVO {
    /**
     * 报送主体名称
     */
    private String mainName;

    /**
     * 报告主体的统一社会信用代码
     */
    private String mainLicenseCode;
    /**
     * 交易对手方名称
     */
    private String clientName;

    /**
     * 交易对手方的统一社会信用代码
     */
    private String clientLicenseCode;

    /**
     * 交易确认书编号
     */
    private String tradeConfirmCode;

    /**
     * 多腿编号
     */
    private Integer confirmId;

    /**
     * 交易确认时间
     */
    private LocalDate confirmDate;

    /**
     * 持仓日期
     */
    private LocalDate positionDate;

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
     * 期权行权时间类型 当工具类型为期权时，填写此项: 1. 欧式期权: EU 2. 美式期权: US 3. 百慕大期权: BD 4. 其他类型为: O 若工具类型不为期权则不填
     */
    private String exerciseType;
    /**
     * 期权行权时间类型
     * 1: 看跌,
     * 2: 看涨,
     * 3: 选择期权,
     * 9: 其他
     * 当期权类型为雪球或者凤凰时，填写敲入状态时香草期权的期权权利类型（如敲入转看跌雪球填1）
     * 1. 看涨雪球及其他看跌期权选1
     * 2. 看跌雪球及其他看涨期权选2
     * 3. 二元小雪球选9
     * 4. 若出现上述提及情况以外的，进行提示，后续进行增添
     */
    private String  optionRightsType;

    /**
     * 产品结构 VA：香草（VANILLA） RE:风险逆转（RISK REVERSAL） CO:三领口(COLLAR) STR:跨式(STRADDLE) STA:宽跨式(STANGLE) VE:价差组合(VERTICLE)
     * SP:标的价差期权(SPREAD) PA:自动敲出可赎回-凤凰式(PHENIX AUTOCALL) SA:自动敲出可赎回-雪球(SNOWBALL AUTOCALL) SF:单鲨(SHARK FIN)
     * DI:二元(DIGITAL) AS:亚式(ASIAN) TL:三层阶梯(THREE LADDER) DD:二元凹式(DIGITAL DOWN) DU:二元凸式(DIGITAL UP) DSF:双鲨(DOUBLE SHARK
     * FIN) RA:区间累计(RANGE ACCURAL) OT:一触即付(ONE TOUCH) DNT:双不触碰(DOUBLE NO TOUCH) O:其他(OTHER) 注意： 1. 累计期权全部选择RA 2.
     * 若出现特殊情况的，进行提示，后续进行增添
     */
    private String structureType;

    /**
     * 参与率
     */
    private String participationRate;

    /**
     * 是否为年化期权
     */
    private String isAnnualizedOption;

    /**
     * 标的资产类型 详细分类请见场外品种分类表
     */
    private String underlyingAssetType;

    /**
     * 标的资产品种 参见标的品种代码表
     */
    private String varietyCode;

    /**
     * 标的资产对应合约
     */
    private String exchangeUnderlyingCode;

    /**
     * 执行价格
     */
    private BigDecimal strike;

    /**
     * 合约估值时标的价格
     */
    private BigDecimal lastPrice;


    /**
     * 合约价值
     */
    private BigDecimal availableAmount;

    /**
     * 估值方法 • M= 按市值计价 • O= 按模型计价 （使用SO定价） • C= 中央对手方估值
     */
    private String valuationType;
    /**
     * 总名义金额
     */
    private BigDecimal notionalPrincipal;

    /**
     * 交易名义金额 该交易累计已平仓的名义本金
     */
    private BigDecimal closeNotionalPrincipal;

    /**
     * 价格符号
     */
    private String priceSymbol;

    /**
     * 计价货币
     */
    private String invoicingCurrency;

    /**
     * 外汇对人民币汇率
     */
    private String exchangeRate;

    /**
     * 总名义数量
     */
    private BigDecimal totalVolume;

    /**
     * 已平仓总名义数量
     */
    private BigDecimal closeVolume;

    /**
     * 数量单位
     */
    private String point;

    /**
     * 估值波动率
     */
    private BigDecimal valuationVol;

    /**
     * 一天有效天数 默认使用245
     */
    private Integer tradeDayByYear;

    private BigDecimal delta;

    private BigDecimal gamma;

    private BigDecimal theta;

    private BigDecimal vega;

    /**
     * 无风险利率变化对期权价值的影响
     */
    private BigDecimal rho;

    private BigDecimal deltaCash;

    /**
     * 无风险利率
     */
    private BigDecimal riskFreeInterestRate;

    /**
     * 贴现率
     */
    private BigDecimal dividendYield;


    private BigDecimal gammaCash;

    /**
     * 到期日
     */
    private LocalDate maturityDate;

    /**
     * 雪球分类 1.传统雪球和带敲出观察空窗的传统雪球； 2.保底雪球；（限亏雪球） 3.阶梯价雪球； 4.小雪球； 9.其他。 雪球期权分类说明详见附件。
     */
    private String snowType;

    /**
     * 雪球期权状态
     */
    private String snowStatus;

    /**
     * 雪球结构要素
     */
    private String snowStructureType;

    /**
     * 交易对手类型
     */
    private String clientType;

    /**
     * 敲出观察日期
     */
    private String obsKnockOutDate;

    /**
     * 敲出观察价格
     */
    private String obsKnockOutPrice;

    /**
     * 敲入观察日期
     */
    private String obsKnockInDate;

    /**
     * 敲入观察价格
     */
    private String obsKnockInPrice;

    /**
     * 标的初始价格
     */
    private BigDecimal entryPrice;

    /**
     * 期权费的费率
     */
    private String optionPremiumPercent;

    /**
     * 保底比例
     * 如果雪球分类为2，该字段填写保底比例，以百分比形式填入，带百分号；
     * 其他有保底性质的雪球，比如同时具有保底和阶梯价的雪球，该字段也应填写保底比例，以百分比形式填入，带百分号；
     * 其余情况，该字段不用填写。
     */
    private String minimumGuaranteeRatio;


    /**
     * 期初保证金
     */
    private BigDecimal initMargin;

    /**
     * 持仓日保证金
     */
    private BigDecimal margin;

    /**
     * 风险中性收益率
     * 即无风险收益率-分红利率。
     */
    private  BigDecimal riskNeutralReturnRate;

    /**
     * 红利票息率
     */
    private BigDecimal bonusRateStructValue;

    /**
     * 敲出票息率
     */
    private  BigDecimal knockOutRebateRate;

    /**
     * 敲入期权类型
     * 指敲入事件发生时生效的期权类型。
     * 1.香草看跌期权（比如普通看涨雪球）
     * 2.香草看涨期权（比如普通看跌雪球）
     * 3.熊市价差期权（比如限亏看涨雪球）
     * 4.牛市价差期权（比如限亏看跌雪球）
     * 9.其他（比如小雪球）
     */
    private String knockInOptionType;

    /**
     * 备注
     */
    private String remarks;
}
