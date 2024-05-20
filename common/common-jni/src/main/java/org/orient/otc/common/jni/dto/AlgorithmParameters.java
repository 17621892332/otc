package org.orient.otc.common.jni.dto;

import lombok.Data;

/**
 * 雪球定价方法
 */
@Data
public class AlgorithmParameters {
    /**
     * 计算雪球价格使用的算法
     */
    String algorithmName;
    /**
     * 蒙特卡洛模拟的路径数量
     */
    int mcNumberPaths;
    /**
     * 偏微分方程网格时间维度的格点数量
     */
    int pdeTimeGrid;
    /**
     * 偏微分方程网格状态维度（标的价格）的格点数量
     */
    int pdeSpotGrid;
}
