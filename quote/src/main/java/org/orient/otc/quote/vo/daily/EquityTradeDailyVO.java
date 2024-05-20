package org.orient.otc.quote.vo.daily;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 权益持仓日报
 * @author dzrh
 */
@Data
public class EquityTradeDailyVO {
    /**
     * 报送主体名称
     */
    private String  mainName;

    /**
     * 报告主体的统一社会信用代码
     */
    private String  mainLicenseCode;
    /**
     * 交易对手方名称
     */
    private String clientName;

    /**
     * 交易对手方的统一社会信用代码
     */
    private String clientLicenseCode;
    /**
     * 主协议类型
     */
    private  String protocolType;
    /**
     * 主协议日期
     */
    private LocalDate protocolDate;

    /**
     * 必填项，只能填Y、N，若报送日期大于最新授信记录的到期日期，也为N
     */
    private String isHaveCredit;
    /**
     * 授信额度
     */
    private BigDecimal credit;

    /**
     * 初始保证金
     */
    private BigDecimal initMargin;

    /**
     * 维持保证金
     */
    private BigDecimal margin;
    /**
     * 操作类型
     * NT、FU、PU、ED
     * （若填CR或ER或MD，在对应的原上报任务中申请重报，如需修改1月4日的报告，在1月4日任务中重新报送）
     * NT：新交易
     * FU：完全解约
     * PU：部分解约
     * ED：终止事件等
     */
    private  String operationType;
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
     * 生效日期
     */
    private LocalDate tradeDate;

    /**
     * 到期日
     */
    private  LocalDate maturityDate;

    /**
     * 行权日
     */
    private  LocalDate strikeDate;

    /**
     * 提前终止日期
     */
    private LocalDate closeDate;

    /**
     * 填报方方向
     * 东证润和买卖方向:
     * 东证润和买为B
     * 东证润和卖为S
     */
    private String buyOrSell;

    /**
     * 资产类型
     * 1. 标的资产以 .SH结尾的，以IF、IC、IM、IH、IO、MO、HO开头的为权益类
     * 2. TS、TF、T、TL开头的为利率类
     * 3. 若出现上述提及情况以外的，进行提示，后续进行增添
     */
    private String assetType;

    /**
     * 工具类型
     * SW:掉期
     * OP:期权
     * FW:远期
     * O:其他
     * 1. 结构类型为香草期权、亚式期权、累计期权、雪球的为期权类
     * 2. 结构类型为远期的则为远期
     * 3. 若出现上述提及情况以外的，进行提示，后续进行增添
     */
    private String toolType;

    /**
     * 期权行权时间类型
     * 当工具类型为期权时，填写此项:
     * 1. 欧式期权: EU
     * 2. 美式期权: US
     * 3. 百慕大期权: BD
     * 4. 其他类型为: O
     * 若工具类型不为期权则不填
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
     * 产品结构
     * VA：香草（VANILLA）
     * RE:风险逆转（RISK REVERSAL）
     * CO:三领口(COLLAR)
     * STR:跨式(STRADDLE)
     * STA:宽跨式(STANGLE)
     * VE:价差组合(VERTICLE)
     * SP:标的价差期权(SPREAD)
     * PA:自动敲出可赎回-凤凰式(PHENIX AUTOCALL)
     * SA:自动敲出可赎回-雪球(SNOWBALL AUTOCALL)
     * SF:单鲨(SHARK FIN)
     * DI:二元(DIGITAL)
     * AS:亚式(ASIAN)
     * TL:三层阶梯(THREE LADDER)
     * DD:二元凹式(DIGITAL DOWN)
     * DU:二元凸式(DIGITAL UP)
     * DSF:双鲨(DOUBLE SHARK FIN)
     * RA:区间累计(RANGE ACCURAL)
     * OT:一触即付(ONE TOUCH)
     * DNT:双不触碰(DOUBLE NO TOUCH)
     * O:其他(OTHER)
     * 注意：
     * 1. 累计期权全部选择RA
     * 2. 若出现特殊情况的，进行提示，后续进行增添
     */
    private String structureType;

    /**
     * 标的资产类型
     * 详细分类请见场外品种分类表
     */
    private String underlyingAssetType;

    /**
     * 标的资产品种
     * 参见标的品种代码表
     */
    private String varietyCode;

    /**
     * 标的资产对应合约
     */
    private String exchangeUnderlyingCode;
    /**
     * 标的资产交易场所
     */
    private String  exchange;

    /**
     * 总名义数量
     */
    private  BigDecimal totalVolume;

    /**
     * 交易名义数量
     */
    private  BigDecimal tradeVolume;

    /**
     * 数量单位
     */
    private  String point;

    /**
     * 参与率
     */
    private  String participationRate;

    /**
     * 是否为年化期权
     */
    private  String isAnnualizedOption;
    /**
     * 标的资产进场价格
     */
    private BigDecimal entryPrice;

    /**
     * 执行价格
     */
    private  BigDecimal strike;

    /**
     * 价格符号
     */
    private  String priceSymbol;

    /**
     * 计价货币
     */
    private String invoicingCurrency;

    /**
     * 外汇对人民币汇率
     */
    private  String exchangeRate;

    /**
     * 总名义金额
     */
    private  BigDecimal notionalPrincipal;

    /**
     * 交易名义金额
     */
    private  BigDecimal tradeNotionalPrincipal;

    /**
     * 期权费金额
     */
    private BigDecimal tradeAmount;

    /**
     * 结算方式
     * 达成一致的结算办法。
     * C =现金
     * P =实物
     * O =其他
     */
    private  String settlementMethod;

    /**
     * 最后结算日
     * 平仓或者到期或者敲出时才需要填写。
     */
    private LocalDate lastSettlementDate;
    /**
     * 结算价确认方式
     * 平仓或者到期或者敲出时才需要填写；
     * 1=一口价
     * 2=点价
     * 3=均价
     * 9=其他
     * 1. 若敲出则选9
     * 2. 其他情况需要增添字段以表示平仓价如何获得
     */
    private String  settlementConfirmType;

    /**
     * 一口价价格
     * 平仓或者到期或者敲出时才需要填写；
     * 如果结算价确认方式=一口价时，填写此项
     */
    private BigDecimal buyItNowPrice;

    /**
     * 商品参考价格
     */
    private BigDecimal underlyingPrice;
    /**
     * delta cash
     */
    private BigDecimal deltaCash;
}
