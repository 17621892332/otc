package org.orient.otc.common.jni.dto;

import lombok.Data;

/**
 * 波动率曲面
 */
@Data
public class VolSurface {
    /**
     * 波动率曲面横轴所在的内存位置
     */
    double[] horizontalAxis;

    /**
     * 波动率曲面横轴的长度
     */
    int horizontalAxisLength;

    /**
     * 波动率曲面纵轴所在的内存位置
     */
    double[] verticalAxis;

    /**
     * 波动率曲面纵轴的长度
     */
    int verticalAxisLength;

    /**
     * 拉平的波动率曲面所在的内存位置
     */
    double[] flattenedVol;

    /**
     * 拉平的波动率曲面长度
     */
    int flattenedVolLength;

}
