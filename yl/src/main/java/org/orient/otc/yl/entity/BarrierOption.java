package org.orient.otc.yl.entity;

import lombok.Data;

/**
 * 障碍期权
 * @author dzrh
 */
@Data
public class BarrierOption {
    /**
     * 连离类型
     */
    private String discrete;
    /**
     * 障碍类型
     */
    private String barrierType;
    /**
     * 障碍价格
     */
    private double barrierPrice;
    /**
     * 高障碍价格
     */
    private double upperBarrierPrice;
    /**
     * 障碍偏移
     */
    private double barrierShift;
    /**
     * 补偿支付类型
     */
    private String rebateType;
    /**
     * 补偿金额
     */
    private double rebate;
    /**
     * 补偿金额比例（名义本金成交方式会用该字段）
     */
    private double rebateRate;
    /**
     * 敲入敲出状态
     */
    private String knockInOutStatus;
    /**
     * 敲入敲出日期
     */
    private String knockInOutDate;
    /**
     * 自定义观察日
     */
    private String observationDates;

}
