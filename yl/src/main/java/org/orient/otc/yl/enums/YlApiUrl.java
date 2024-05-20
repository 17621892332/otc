package org.orient.otc.yl.enums;

import lombok.Getter;

/**
 * @author dzrh
 */
@Getter
public enum YlApiUrl {
        /*
        otcTradeList = L"/api/v2/clientPositionList",
        otcTradeListV1 = L"/api/v1/clientPositionList",
        //场内期权信息是不是已经同步了？
        listedTradeInfoList = L"/api/v1/getExchangeOptionList",
        listedFutureInfoList = L"/api/v1/getUnderlyingList",
        //场内实时持仓是不是也已经同步了？
        listedTradeList = L"/api/v1/todayExchangePositions",
        realtimeListedTradeList = L"api/v1/realtimeExchangePositions",
        closeTrade = L"/api/v1/TradeClose",
        closeTradesInfo = L"api/v1/tradeCloseInfos", //YC后来改为批量模糊查询
        openCloseInfos = L"api/v1/otc-option/open_close_infos", //单笔精确查询，但测试后发现并没有做到精确查询
        updateRisk = L"/api/v1/updateCustomTradeRisk",  //风险维护地址
        uploadVol = L"/api/v2/saveVolatility";      // upload vol surface*/
    /**
     * 客户信息列表
     */
    CLIENT_INFO_LIST("/api/v1/clientInfoList"),
    /**
     * 客户详情
     */
    GET_CLIENT_INFO("/api/v1/GetClientInfo"),
    /**
     * 客户详情2
     */
    GET_CLIENT_INFO2("/api/v1/GetClientInfo2"),

    /**
     * 	获取标的资产历史日期结算价
     */
    GET_UNDERLYING_EOD_PRICES("/api/v1/underlying/eod_prices"),

    /**
     * 客户持仓信息
     */
    CLIENT_POSITION_LIST_V3("/api/v3/clientPositionList"),
    CLIENT_POSITION_LIST_V2("/api/v2/clientPositionList"),
    CLIENT_POSITION_LIST_V1("/api/v1/clientPositionList"),
    /**
     * 获取标的基础信息
     */
    GET_UNDERLYING_LIST("/api/v1/getUnderlyingList"),
    /**
     * 获取波动率曲面
     */
    GET_UNDERLYING_VOL_SURFACE("/api/v1/getUnderlyingVolSurface"),

    /**
     * 修改波动率曲面
     */
    SAVE_VOLATILITY("/api/v2/saveVolatility"),
    /**
     * 场外期权交易录入
     */
    ORDER_OPTION_URL("/api/v1/order/option"),

    /**
     * 场外期权结构化交易录入
     */
    STRUCTURE_OPTION("/api/v1/order/structure_options"),
    /**
     * 场外期权远期交易录入
     */
    FORWARD_TRADE_SAVE("/api/v1/order/ForwardTradeSave"),

    /**
     *  删除交易
     */
    TRADE_INVALID("/api/v1/trade/invalid"),
    /**
     * 场外交易了解明细
     */

    OPEN_CLOSE_INFOS("/api/v1/otc-option/open_close_infos"),

    /**
     * 了结场外期权交易
     */
    TRADE_CLOSE("/api/v1/TradeClose"),

    /**
     * 访问令牌(access_token)
     */
    OAUTH_TOKEN_URL("/api/token?grant_type=client_credential"),

    /**
     * 客户出入金
     */
    CLIENT_CASH_IN_CASH_OUT("/api/v1/ClientCashInCashOut"),

    /**
     * 客户出入金确认
     */
    CLIENT_CASH_IN_CASH_OUT_CONFIRM("/api/v1/ClientCashInCashOutConfirm"),

    /**
     * 客户出入金拒绝
     */
    CLIENT_CASH_IN_CASH_OUT_REJECT("/api/v1/ClientCashInCashOutReject"),

    /**
     * 更新持仓保证金
     */
    UPDATE_TRADE_POSITION_MARGIN("/api/v1/updateTradePositionMargin"),

    /**
     * 更新持仓风险
     */
    UPDATE_CUSTOM_TRADE_RISK("/api/v1/updateCustomTradeRisk"),
    ;

    YlApiUrl(String path) {
        this.path = path;
    }

    private final String path;

}
