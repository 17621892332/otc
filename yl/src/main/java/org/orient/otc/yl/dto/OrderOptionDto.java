package org.orient.otc.yl.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.orient.otc.yl.entity.MetaDic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基本交易要素字段
 * @author dzrh
 */
@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOptionDto {

    /**
     * 是否更新
     */
    @JSONField(name = "_update")
    private boolean update;
    /**
     * 年化系数, 1即为100%, 如果IsAnnualized为true而没有填写则系统自动计算, 必须大于等于0
     */
    private BigDecimal annualizeFactor;
    /**
     * 簿记账户名称
     */
    private String assetBookName;
    /**
     * 交易方向:  买入|卖出
     */
    private String buySell;
    /**
     * 易对手方名称 -- 交易对手方编号存在则忽略此值
     */
    private String clientName;
    /**
     * 交易对手方编号-- 优先使用
     */
    private String clientNumber;
    /**
     * 交易备注,最多100个字
     */
    private String comments;
    /**
     * 分红率,默认0,股票标的适用, 必须大于等于0
     */
    private BigDecimal dividendRate;
    /**
     * 到期日期,格式: yyyy-MM-dd
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate exerciseDate;
    /**
     * 行权方式:American|European, 障碍期权/双鲨期权/凤凰期权/雪球期权/合成价差期权不支持美式行权
     */
    private String exerciseMode;
    /**
     * 初始保证金
     */
    private BigDecimal initialMargin;
    /**
     * 期权年化, 默认fale
     */
    private String isAnnualized;
    /**
     * 是否相对行权价: 是|否,默认否
     */
    private String isMoneynessOption;
    /**
     * 无风险利率, 默认为系统配置的全局无风险利率,不能小于0, 必须大于等于0
     */
    private BigDecimal noRiskRate;
    /**
     * 交易份额, 优先级3(参考表后备注)
     */
    private BigDecimal notional;
    /**
     * 平滑过渡天数, 必须大于0
     */
    private long numOfSmoothingDays;
    /**
     * 看涨看跌: Call|Put
     */
    private String optionType;
    /**
     * 参与率, 默认1, 必须大于等于0
     */
    private BigDecimal participationRate;
    /**
     * 期权费率, 优先级2, 参考表后备注
     */
    private BigDecimal premiumRate;
    /**
     * 保底收益率, 默认0, 必须大于等于0
     */
    private BigDecimal principalRate;
    /**
     * 结算日期,格式: yyyy-MM-dd,不填则默认为到期日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate settlementDate;
    /**
     * 结算方式, 默认0 0：收盘价，1：结算价
     */
    private long settlementType;
    /**
     * 标的价格 -- 组合标的时忽略此项数据,由组合标的数据合成
     */
    private BigDecimal spotPrice;
    /**
     * 名义本金(组合标的时取最大名义本金), 优先级2(参考表后备注)
     */
    private BigDecimal stockEqvNotional;
    /**
     * 实际名义本金(组合标的时取最大实际名义本金), 优先级1(参考表后备注)
     */
    private BigDecimal stockEqvNotionalReal;
    /**
     * 执行价格,IsMoneynessOption如果等于'是'则填写相对行权价(行权价/标的价格),否则为绝对行权价
     */
    private BigDecimal strike;
    /**
     * 交易数量, 优先级4(参考表后备注)
     */
    private BigDecimal tradeAmount;
    /**
     * 目标波动率, 必须大于等于0
     */
    private BigDecimal tradeCloseVolatility;
    /**
     * 成交日期,格式: yyyy-MM-dd
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate tradeDate;
    /**
     * 交易编号,不填写则自动生成
     */
    private String tradeNumber;
    /**
     * 成交波动率, 必须大于等于0
     */
    private BigDecimal tradeOpenVolatility;
    /**
     * 权利金总额, 优先级3, 参考表后备注
     */
    private BigDecimal tradePrice;
    /**
     * 交易员名称 -- 优先使用登录名进行匹配，如果匹配不上则使真实姓名匹配
     */
    private String traderName;
    /**
     * 权利金单价, 优先级1, 参考表后备注
     */
    private BigDecimal tradeSinglePrice;
    /**
     * 结构类型 -- 参见附录:场外期权交易结构类型
     */
    private String tradeType;
    /**
     * 标的代码
     */
    private String underlyingCode;

    /**
     * 均价起算日期
     */
    private LocalDateTime averagingPeriodStartDate;

    /**
     * 结算方式
     */
    private  String payoffType;

    /**
     * 执行价
     */
    private BigDecimal enhancedPrice;

    /**
     * day1&&midVol
     */
    private MetaDic metaDic;
    /**
     * 结构类型
     */
    private String structureType;

    /**
     * 自定义交易内容
     */
    private String extendInfo;
    //雪球期权交易要素start
    /**
     * 敲出障碍价格
     */
    private BigDecimal kOBarrier;
    /**
     * 敲出赔付类别，0：票息补偿，1： 敲出转期权，2：敲出转价差期权
     */
    private int kOPayoffType;
    /**
     * 票息年化，false: 是,true:否
     */
    private Boolean isFixedCoupon;
    /**
     * 敲出票息率
     */
    private BigDecimal kORebate;
    /**
     * 票息日历规则
     */
    private String couponDayCount;
    /**
     * 年化期权费率
     */
    private BigDecimal annualizedPremiumRate;
    /**
     * 敲出期权行权价2
     */
    private BigDecimal spreadStrikeAtKO;
    /**
     * 敲出期权行权价1
     */
    private BigDecimal spreadStrikeAtKO1;

    /**
     * 敲出支付方式，0：立即，1：期末
     */
    private int kORebateType;

    /**
     * 敲出观察日
     * 敲出观察频率，格式：逗号隔开的观察日列表 + 分号(;) + 逗号隔开障碍价格列 + 分号(;) +逗号隔开的票息列表。
     */
    private String kOObservationDates;
    /**
     * 票息支付日期
     */
    private String kOObservationSettleDates;
    /**
     * 敲入观察日
     */
    private String observationDates;
    /**
     * 敲入障碍价格
     */
    private BigDecimal kIBarrier;
    /**
     * 敲入赔付类别，0：无，1：敲入转看跌，2：敲入转熊市价差，3：敲入转看涨，4：敲入转牛市价差
     */
    private int kIPayoffType;
    /**
     * 敲入权行权价
     */
    private BigDecimal spreadStrike1;
    /**
     * 封顶/封底权价，敲入到期支付类别为2和4时使用，和行权价采用同样的输入方式
     */
    private BigDecimal spreadStrike;
    /**
     * 红利票息
     */
    private BigDecimal coupon;
    /**
     * 期权年化
     */
    private boolean isAnnualized2;
    /**
     * 年化系数
     */
    private BigDecimal annualizeFactor2;
    /**
     * null | KnockedIn | KnockedOut
     */
    private String knockInOutStatus;
    /**
     * 观察中 | 已敲入 | 已敲出
     */
    private String knockInOutStatusCn;
    /**
     * 敲入敲出时间
     */
    private String knockInOutDate;
    //雪球期权交易要素end
}
