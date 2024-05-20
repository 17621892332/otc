package org.orient.otc.api.quote.dto.risk;

import lombok.Data;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.TradeObsDateVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 风险计算响应
 */
@Data
public class TradeRiskCacularResult {
    private String id;

    /**
     * 场内账号ID
     */
    private String investorId;

    /**
     * 场外or场内
     */
    private TradeRiskCacularResultSourceType tradeRiskCacularResultSourceType;
    /**
     * 期权or期货
     */
    private TradeRiskCacularResultType tradeRiskCacularResultType;

    /**
     * 组合编号
     */
    private String combCode;

    /**
     * 交易编号
     */

    private String tradeCode;


    /**
     * 关联交易编号
     */
    private String relevanceTradeCode;

    /**
     * 合约代码
     */
    private String underlyingCode;

    /**
     * 场内标的资产码
     */
    private String exchangeUnderlyingCode;


    /**
     * 产品开始日期
     */
    private LocalDate productStartDate;

    /**
     * 标的名称
     */
    private String underlyingName;
    /**
     * 合约乘数
     */
    private Integer multiplier;


    /**
     * 品种id
     */
    private Integer varietyId;

    /**
     * 品种代码
     */
    private String varietyCode;

    /**
     * 采价次数
     */
    private Integer obsNumber;
    /**
     * 观察日列表
     */
    private List<TradeObsDateVO> obsDateList;

    /**
     * 期权代码
     */
    private String instrumentId;
    /**
     * 分红率
     */
    private BigDecimal dividendYield;
    /**
     * 无风险利率
     */
    private BigDecimal riskFreeInterestRate;


    /**
     * 入场价格
     */
    private BigDecimal entryPrice;

    /**
     * 簿记id
     */
    private Integer assetId;

    /**
     * 合约实时行情
     */
    private BigDecimal lastPrice;

    /**
     * 期权组合类型
     */
    private OptionCombTypeEnum optionCombType;

    /**
     * 期权类型
     */
    private OptionTypeEnum optionType;

    /**
     * 看涨看跌
     */
    private CallOrPutEnum callOrPut;

    /**
     * 行权价格
     */
    private BigDecimal strike;

    /**
     * 客户方向
     */
    private BuyOrSellEnum buyOrSell;

    /**
     * 到期日
     */
    private LocalDate maturityDate;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 单位固定赔付
     */
    private BigDecimal fixedPayment;

    /**
     * 每日数量
     */
    private BigDecimal basicQuantity;
    /**
     * 结算方式
     */
    private SettleTypeEnum settleType;
    /**
     * 多倍系数
     */
    private BigDecimal leverage;

    /**
     * 敲出价格
     */
    private BigDecimal barrier;

    /**
     * 执行价斜坡
     */
    private BigDecimal strikeRamp;

    /**
     * 障碍价斜坡
     */
    private BigDecimal barrierRamp;

    /**
     * 敲出赔付
     */
    private BigDecimal knockoutRebate;

    /**
     * 到期倍数
     */
    private BigDecimal expireMultiple;

    //雪球Start
    /**
     * 定价方法：PDE/MC
     */
    private String algorithmName;

    /**
     * 返息率
     */
    private BigDecimal returnRateStructValue;

    /**
     * 返息率是否年化 0 否 1是
     */
    private Boolean returnRateAnnulized;

    /**
     * 红利票息
     */
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    private Boolean bonusRateAnnulized;

    /**
     * 敲入价格
     */
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入价格是否为相对水平值
     */
    private Boolean knockinBarrierRelative;
    /**
     * 敲入价格Shift
     */
    private BigDecimal knockinBarrierShift;

    /**
     * 是否敲入
     */
    private Boolean alreadyKnockedIn;

    /**
     * 敲入障碍
     */
    private BigDecimal strikeOnceKnockedinValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean strikeOnceKnockedinRelative;
    /**
     * 敲入障碍Shift
     */
    private BigDecimal strikeOnceKnockedinShift;

    /**
     * 敲入障碍2
     */
    private BigDecimal strike2OnceKnockedinValue;

    /**
     * 敲入障碍2是否为相对水平值
     */
    private Boolean strike2OnceKnockedinRelative;
    /**
     * 敲入障碍2Shift
     */
    private BigDecimal strike2OnceKnockedinShift;
    //雪球End

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 存续数量
     */
    private BigDecimal availableVolume;

    /**
     * 名义本金
     */
    private BigDecimal notionalPrincipal;
    /**
     * 存续名义本金
     */
    private BigDecimal availableNotionalPrincipal;

    /**
     * 期权￥单价
     */
    private BigDecimal optionPremium;

    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 存续单价
     */
    private BigDecimal availablePremium;

    /**
     * 存续总额
     */
    private BigDecimal availableAmount;

    /**
     * 昨日存续总额
     */
    private BigDecimal lastTradeDayAvailableAmount;
    /**
     * 今日开仓总额
     */
    private BigDecimal todayOpenAmount;

    /**
     * 今日平仓总额
     */
    private BigDecimal todayCloseAmount;

    /**
     * 累计盈亏
     */
    private BigDecimal totalProfitLoss;

    /**
     * 今日盈亏
     */
    private BigDecimal todayProfitLoss;

    /**
     * 实现盈亏
     */
    private BigDecimal positionProfitLoss;

    /**
     * 开仓波动率
     */
    private BigDecimal tradeVol;

    /**
     * 当前波动率
     */
    private BigDecimal nowVol;

    private BigDecimal accumulatedPosition;

    private BigDecimal accumulatedPayment;

    private BigDecimal accumulatedPnl;

    private BigDecimal todayAccumulatedPosition;

    private BigDecimal todayAccumulatedPayment;

    private BigDecimal todayAccumulatedPnl;

    /**
     * 计算波动率
     */
    private BigDecimal riskVol;

    private Integer clientId;

    private BigDecimal delta;

    private BigDecimal deltaLots;

    private BigDecimal deltaCash;

    private BigDecimal gamma;

    private BigDecimal gammaLots;

    private BigDecimal gammaCash;

    private BigDecimal theta;

    private BigDecimal vega;

    /**
     * 无风险利率变化对期权价值的影响
     */
    private BigDecimal rho;

    /**
     * 股息率变化对期权价值的影响
     */
    private BigDecimal dividendRho;

    private BigDecimal day1PnL;

    /**
     * 计算的时间戳
     */
    private Long cacularTime;

    /**
     * 最后一次计算的状态
     */
    private SuccessStatusEnum status;

    /**
     * 交易状态
     */
    private TradeStateEnum tradeState;

    private LocalDate riskDate;

    /**
     * 是否敲出
     */
    private Boolean isKnockOut;
}
