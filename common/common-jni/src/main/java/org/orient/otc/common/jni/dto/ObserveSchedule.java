package org.orient.otc.common.jni.dto;

import lombok.Data;

/**
 * 观察日列表
 */
@Data
public class ObserveSchedule {
    /**
     * 采价日序列
     */
    long observeDate;

    /**
     * 已采集价格序列
     */
    double fixedPrice;
}
