package org.orient.otc.common.jni.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 隐含波动率返回值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIBlackImpliedVolResult {
    double volatility;

    String errorMessage;
}
