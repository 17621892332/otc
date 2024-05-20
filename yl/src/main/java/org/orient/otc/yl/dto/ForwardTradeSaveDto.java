package org.orient.otc.yl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 基本交易要素字段
 * @author dzrh
 */
@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardTradeSaveDto {
    /**
     * 值为0双标的录入模式（双标的有标准模式和挂钩标的模式，需要在admin页面设置），值其他单标的录入模式
     */
    Integer priceModel;
    /**
     * 交易代码
     */
    String tradeNumber;
    /**
     * 合约代码
     */
    String underlyingCode;
    /**
     * 交易员名称 -- 优先使用登录名进行匹配，如果匹配不上则使用真实姓名匹配
     */
    String traderName;
    /**
     * 交易对手方编号-- 优先使用
     */
    String clientNumber;
    /**
     * 交易对手方名称 -- 交易对手方编号存在则忽略此值
     */
    String clientName;
    /**
     * 簿记账户名称
     */
    String assetBookName;
    /**
     * 交易方向:  买入|卖出
     */
    String buySell;
    /**
     * 成交日期,格式: yyyy-MM-dd
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    LocalDate tradeDate;
    /**
     * 到期日期,格式: yyyy-MM-dd
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    LocalDate exerciseDate;
    /**
     * 结算日期,格式: yyyy-MM-dd,不填则默认为到期日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    LocalDate settlementDate;
    /**
     * 多空方向
     */
    String optionType;
    /**
     * 手数
     */
    BigDecimal lots;
    /**
     * 成交数量
     */
    BigDecimal tradeAmount;
    /**
     * 交割价格
     */
    BigDecimal strike;
    /**
     * 远期价值
     */
    BigDecimal forwardValue;
    /**
     * 初始保证金
     */
    BigDecimal initialMargin;
    /**
     * 开仓总费用
     */
    BigDecimal tradePrice;
    /**
     * 开仓费用
     */
    BigDecimal openCommission;
    /**
     * 年化仓储成本
     */
    BigDecimal annualStoragePrice;
    /**
     * 无风险利率
     */
    BigDecimal noRiskRate;
    /**
     * 备注
     */
    String comments;
    /**
     * 均价日
     */
    String observationDates;
    /**
     * 标的期初价格1
     */
    BigDecimal spotPrice;
    /**
     * 选择双标的挂钩标的模式 基差必填
     */
    BigDecimal basisGap;
    /**
     * 选择双标的两种模式，基差标的代码 必填
     */
    String basisUnderlyingCode;
    /**
     * 选择双标的两种模式，标的期初价格2必填
     */
    BigDecimal spotPrice2;
    /**
     * 保证金模板（不填系统默认）
     */
    String marginTemplateName;

}
