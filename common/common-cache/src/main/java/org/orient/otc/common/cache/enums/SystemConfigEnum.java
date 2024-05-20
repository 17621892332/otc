package org.orient.otc.common.cache.enums;

/**
 * 系统配置列表
 * @author dzrh
 */
public enum SystemConfigEnum {

    /**
     * 无风险利率
     */
    riskFreeInterestRate,
    /**
     * 股息率
     */
    dividendYield,
    /**
     * 上一个交易日
     */
    lastTradeDay,
    /**
     * 系统交易日
     */
    tradeDay,
    /**
     * 蒙特卡洛模拟的路径数量
     */
    mcNumberPaths,
    /**
     * 偏微分方程网格时间维度的格点数量
     */
    pdeTimeGrid,
    /**
     * 偏微分方程网格状态维度（标的价格）的格点数量
     */
    pdeSpotGrid,
    /**
     * 报送主体名称
     */
    mainName,
    /**
     * 报告主体的统一社会信用代码
     */
    mainLicenseCode,
    /**
     * 模拟路径数
     */
    pathNumber,
    /**
     * 线程数
     */
    threadNumber,
    ;

    SystemConfigEnum() {
    }

}
