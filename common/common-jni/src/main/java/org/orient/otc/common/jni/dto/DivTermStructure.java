package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 期限結構
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DivTermStructure {
    /**
     * 分红率期限结构的时间轴
     */
    double[] timeAxis;

    /**
     * 时间轴数组和分红率数组的长度
     */
    int timeAxisLength;
    /**
     * 分红率期限结构的具体值
     */
    double[] flattenedDiv;
}
