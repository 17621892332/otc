package org.orient.otc.yl.entity;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class SnowballOption {

    /**
     * 敲出障碍价格
     */
    private double kOBarrier;
    /**
     * 敲出赔付类别，0：票息补偿，1： 敲出转期权，2：敲出转价差期权
     */
    private int kOPayoffType;
    /**
     * 票息年化，false: 是,true:否
     */
    private boolean isFixedCoupon;
    /**
     * 敲出票息率
     */
    private double kORebate;
    /**
     * 票息日历规则
     */
    private String couponDayCount;
    /**
     * 年化期权费率
     */
    private double annualizedPremiumRate;
    /**
     * 敲出期权行权价2
     */
    private double spreadStrikeAtKO;
    /**
     * 敲出期权行权价1
     */
    private double spreadStrikeAtKO1;

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
    private double kIBarrier;
    /**
     * 敲入赔付类别，0：无，1：敲入转看跌，2：敲入转熊市价差，3：敲入转看涨，4：敲入转牛市价差
     */
    private int kIPayoffType;
    /**
     * 敲入权行权价
     */
    private double spreadStrike1;
    /**
     * 封顶/封底权价，敲入到期支付类别为2和4时使用，和行权价采用同样的输入方式
     */
    private double spreadStrike;
    /**
     * 红利票息
     */
    private double coupon;
    /**
     * 期权年化
     */
    private boolean isAnnualized2;
    /**
     * 年化系数
     */
    private double annualizeFactor2;
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

}
