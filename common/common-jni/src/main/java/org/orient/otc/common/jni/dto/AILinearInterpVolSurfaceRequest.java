package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 线性插值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AILinearInterpVolSurfaceRequest {
    /**
     * 需计算的波动率所在的期限
     */
    double dimTenor;

    /**
     * 需计算的波动率所在的虚实值位置
     */
    double dimMoneyness;
}
