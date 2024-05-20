package org.orient.otc.common.cache.adapter;

/**
 * @author dzrh
 */
public class RedisAdapter {


    /**
     * ylAccessToken
     */
    public static String YL_ACCESS_TOKEN_DIRECTORY ="user:ylAccessToken:";

    /**
     * 客户端登录的token
     */
    public static String SESSION_DIRECTORY = "user:session:";

    /**
     * 场内账号信息
     */
    public static String EXCHANGE_ACCOUNT = "exchangeAccount";

    /**
     * 场内账号登录状态
     */
    public static String EXCHANGE_ACCOUNT_LOGIN_STATUS="exchangeAccountLoginStatus";

    /**
     *   风险计算结果
     */
    public static String TRADE_RISK_RESULT = "tradeRiskResult";

    /**
     * 风险计算的合约列表
     */
    public static String RISK_UNDERLYING_SET= "riskUnderlyingSet";

    /**
     * 风险计算的合约列表
     */
    public static String OTC_RISK_UNDERLYING_LIST= "otcRiskUnderlyingList";

    /**
     * 风险计算的合约列表
     */
    public static String EXCHANGE_RISK_UNDERLYING_LIST= "exchangeRiskUnderlyingList";

    //market行情
    /**
     * 实时行情reids的key
     */
    public static String REAL_TIME_MARKET = "quote:";
    /**
     * 昨日收盘时合约行情
     */
    public static String TRADE_DAY_CLOSE_MARKET ="tradeDayCloseMarket:";


    /**
     * riceQuantToken
     */
    public static String RICE_QUANT_TOKEN ="riceQuantToken";
    //market行情End
    /**
     * 场内持仓信息
     */
    public static String EXCHANGE_POSITION_INFO = "exchangePositionInfo:";
    /**
     * 补单处理记录
     */
    public static String SUPPLEMENTARY_ORDER ="supplementaryOrder:";

    /**
     *  已平仓的合约累计收益
     */
    public static String TOTAL_PNL_BY_CLOSED="totalPnlByClosed";

    /**
     * 初始化累计盈亏
     */
    public static String INIT_TOTAL_PNL="initTotalPnl";


    /**
     * delta调整值
     */
    public static String DELTA_ADJUSTMENT="deltaAdjustment";

    /**
     * 风险计算时间
     */
    public static String RISK_TIME="riskTime";
    /**
     * 单个合约的收盘价信息
     */
    public static String ALL_CLOSE_DATE_PRICE_BY_CODE ="allCloseDatePriceByCode:";

    /**
     * 系统配置信息
     */
    public static String SYSTEM_CONFIG_INFO = "systemConfigInfo";
    /**
     * 合约信息
     */
    public static String UNDERLYING_BY_CODE ="underlyingInfo:";

    /**
     * 波动率
     */
    public static String VOLATILITY="volatility:";
    /**
     * 风险自定义行情
     */
    public static String RISK_MARK ="riskMark";

    /**
     * 今日开仓金额
     */
    public static String TODAY_OPEN_TRADE_AMOUNT="todayOpenTradeAmount:";

    /**
     * 今日平仓金额
     */
    public static String TODAY_CLOSE_TRADE_AMOUNT ="todayCloseTradeAmount:";

    /**
     * 交易最新的风险快照
     */
    public static String TRADE_LAST_RISK_INFO="tradeLastRiskInfo:";
    /**
     * tdAppToken
     */
    public static String TD_APP_TOKEN ="user:tdAppToken:";
    /**
     * tdAccessToken
     */
    public static String TD_ACCESS_TOKEN ="user:tdAccessToken:";

}
