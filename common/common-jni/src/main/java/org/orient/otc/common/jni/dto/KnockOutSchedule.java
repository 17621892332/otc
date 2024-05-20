package org.orient.otc.common.jni.dto;

import lombok.Data;

/**
 * 敲出观察列表
 */
@Data
public class KnockOutSchedule {
    /**
     * 敲出观察日期对应的时间戳
     */
    long observeDate;

    /**
     * 该观察日对应的敲出障碍价格
     */
    double barrier;

    /**
     * 是否为相对障碍价格
     */
    boolean barrierRelative;

    /**
     * 该观察日对应的敲出票息
     */
    double rebateRate;

    /**
     * 敲出票息是否年化
     */
    boolean rebateRateAnnulized;

    /**
     * 资产观察价格
     */
    double fixedPrice;

    /**
     * 敲出边界调整
     */
    double barrierShift;
}
