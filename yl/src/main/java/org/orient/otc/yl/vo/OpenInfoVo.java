package org.orient.otc.yl.vo;

import lombok.Data;
import org.orient.otc.yl.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dzrh
 */

@Data
public class OpenInfoVo  {

    /**
     * 系统编号
     */
    private int tradeId;
    /**
     * 客户ID
     */
    private int clientId;
    /**
     * 客户编号
     */
    private String clientNumber;
    /**
     * 交易编号
     */
    private String tradeNumber;
    /**
     * 交易方向(交易员角度)
     */
    private String buySell;
    /**
     * 交易日期,格式:2019005-06 00:00:00
     */
    private LocalDateTime tradeDate;
    /**
     * 到期日期,格式:20190-05-06 00:00:00
     */
    private LocalDateTime exerciseDate;
    /**
     * 行权模式(American|European)
     */
    private String exerciseMode;
    /**
     * 看涨看跌(Call|Put)
     */
    private String callPut;
    /**
     * 期权类型
     */
    private String tradeType;

    /**
     * 结构类型
     */
    private String structureType;
    /**
     * 标的代码
     */
    private String underlyingCode;
    /**
     * 标的名称
     */
    private String underlyingName;
    /**
     * 期初标的价格
     */
    private BigDecimal initialSpotPrice;
    /**
     * 执行价格
     */
    private BigDecimal strike;
    /**
     * 成交波动率
     */
    private BigDecimal tradeOpenVolatility;
    /**
     * 权利金（单价）
     */
    private BigDecimal tradeSinglePrice;
    /**
     * 初始保证金
     */
    private BigDecimal initialMargin;
    /**
     * 交易数量
     */
    private BigDecimal originalNotional;
    /**
     *交易总额
     */
    private BigDecimal tradePrice;
    /**
     * 名义本金
     */
    private BigDecimal originalStockEqvNotional;
    /**
     * 参与率
     */
    private BigDecimal articipationRate;
    /**
     * 当前标的价格
     */
    private BigDecimal underlyingPrice;
    /**
     * 持仓波动率
     */
    private BigDecimal vol;
    /**
     * 期权现价（单价）
     */
    private BigDecimal currentPrice;
    /**
     * 持仓数量
     */
    private BigDecimal tradeAmount;
    /**
     * 持仓市值
     */
    private BigDecimal roundedPv;
    /**
     * Delta手数
     */
    private BigDecimal deltaLots;
    /**
     * Gamma手数
     */
    private BigDecimal gammaLots;
    /**
     * Delta
     */
    private BigDecimal delta;
    private BigDecimal gamma;
    private BigDecimal vega;
    private BigDecimal theta;
    private BigDecimal rho;
    /**
     * 行权状态 -8:非自主下单数据，不能自主行权 8:待行权 9:申请行权 10,11:行权处理中 12:行权通过
     */
    private int exercisStatus;
    /**
     * 交易备注
     */
    private String comments;
    /**
     * 交易状态
     */
    private String tradeStatus;
    /**
     * 平仓目标波动率
     */
    private BigDecimal tradeCloseVolatility;
    /**
     * 持仓份额 (20200811 新增)
     */
    private BigDecimal notional;
    /**
     * 成份额 (2200811新增)
     */
    private BigDecimal originaNotional;
    /**
     * 组合ID，大于0则说明是组合交易(20201119 新增)
     */
    private int parentradeId;
    /**
     * 是否名义本金成交方式,为true则权利金和payoff相关值为百分比格式，为false则是数量成交方式并且权利金和payoff相关值为百分比格式(20201119 新增)
     */
    private boolean isUsePremiumRate;
    /**
     * 是否总额成交(20201119 新增)
     */
    private boolean isUseTotalPremium;
    /**
     * 是否相对行权价，用于区分行权价、障碍价格、敲入敲出价格等是采用绝对价格方式还是百分比方式
     */
    private boolean isMoneynessOption;

    /**
     * 年化期权费率
     */
    private double annualizedPremiumRate;

    /**
     * 均价起算日
     */

    private LocalDateTime averagingPeriodStartDate;
    /**
     * 均价起算方式
     */
    private String payoffType;
    /**
     * 障碍期权
     */
    private BarrierOption barrierOption;
    /**
     * 雪球期权 2021-5-26 增加
     */
    private SnowballOption snowballOption;

    /**
     * 远期
     */
    private ForwardOption forward;
    /**
     * 簿记账户名称
     */
    private String assetBookName;

    /**
     * 平仓价多次平仓取均价)
     */
    private BigDecimal unwindPrice;
    /**
     * 平仓价多次平仓取均价)
     */
    private BigDecimal finalPrice;
    /**
     * 对冲波动率
     */
    private BigDecimal hedgeVol;
    /**
     * 最后一次平仓日期，格式:yyyy-MM-dd,20200718增加;
     */
    private LocalDateTime unwindDate;
    /**
     * 拓展信息
     */
    private List<ExtendData> propertys;

    private String extendInfo;

    private MetaDic metaDic;

    private String traderName;

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
     * null | KnockedIn | KnockedOut
     */
    private String knockInOutStatus;

    /**
     * 红利票息
     */
    private BigDecimal coupon;

    /**
     * 票息年化，false: 是,true:否
     */
    private Boolean isFixedCoupon;

    /**
     * 保底收益率, 默认0, 必须大于等于0
     */
    private BigDecimal principalRate;

    /**
     * 是否相对价格
     */
    private Boolean isMoneynessOptionData;
    /**
     * 敲出观察列表
     */
    private  String KOObservationDates;

    /**
     * 敲出结算列表
     */
    private String KOObservationSettleDates;
}
