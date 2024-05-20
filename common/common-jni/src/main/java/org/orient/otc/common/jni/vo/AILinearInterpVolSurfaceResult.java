package org.orient.otc.common.jni.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 线性插值返回值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AILinearInterpVolSurfaceResult {
    double volatility;

    String errorMessage;
}
