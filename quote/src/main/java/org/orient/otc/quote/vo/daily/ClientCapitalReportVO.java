package org.orient.otc.quote.vo.daily;

import lombok.Data;

/**
 * 商品成交日报
 * @author dzrh
 */
@Data
public class ClientCapitalReportVO {
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
     * 子公司给对手方的狭义授信额度
     */
    private String toClientCreditString;
    /**
     * 对手方可后付的权利金额度
     */
    private String clientTradePriceCreditString;
    /**
     * 已使用子公司给的狭义授信额度
     */
    private String clientPositionCreditString;
    /**
     * 持仓权利金支出（后付）
     */
    private String positionTradePriceOutString;


    /**
     * 对手方给子公司的狭义授信额度
     */
    private String toCompanyCreditString;

    /**
     * 子公司可后付的权利金额度
     */
    private String companyTradePriceCreditString;

    /**
     * 已使用对手方给的狭义授信额度
     */
    private String companyPositionCreditString;
    /**
     * 持仓权利金收入（后付）
     */
    private String positionTradePriceInString;


    /**
     * 期初结存
     */
    private String openBalanceString;
    /**
     * 当日出入金
     */
    private String todayCashInCashOutString;
    /**
     * 当日终止收益
     */
    private String todaySettleEarningsString;
    /**
     * 当日开仓权利金收支
     */
    private String todayOpenTradePriceString;
    /**
     * 期末结存
     */
    private String endBalanceString;
    /**
     * 保证金收取模式
     */
    private String marginCollectionMode;
    /**
     * 追保到账时间
     */
    private String marginPayTime;
    /**
     * 对手方保证金占用
     */
    private String clientMarginHoldString;
    /**
     * 对手方已缴的非现金履约保障品类型
     */
    private String nonCashPerformanceGuaranteesType;
    /**
     * 对手方已缴的非现金履约保障品估值
     */
    private String nonCashPerformanceGuaranteesValuation;
    /**
     * 子公司保证金占用
     */
    private String companyMarginHoldString;
    /**
     * 子公司已缴的非现金履约保障品类型
     */
    private String childNonCashPerformanceGuaranteesType;
    /**
     * 子公司已缴的非现金履约保障品估值
     */
    private String childNonCashPerformanceGuaranteesValuation;
    /**
     * 可用资金
     */
    private String availableCashString;

    /**
     * 可取资金
     */
    private String desirableFundString;

    /**
     * 应追加资金
     */
    private String addFundsString;

    /**
     * 持仓市值
     */
    private String positionValueString;

    /**
     * 市值权益
     */
    private String marketValueEquityString;

    /**
     * 备注
     */
    private String remark;
}
