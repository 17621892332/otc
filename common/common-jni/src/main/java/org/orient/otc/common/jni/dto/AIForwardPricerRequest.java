package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 远期
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIForwardPricerRequest {
    /**
     * 标的合约价格
     */
    double s;

    /**
     * 执行价
     */
    double k;
}
