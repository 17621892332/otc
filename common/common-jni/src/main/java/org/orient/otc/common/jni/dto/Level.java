package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 观察对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Level {
    /**
     * 水平值
     */
    double levelValue;

    /**
     * 是否为相对水平值
     */
    boolean levelRelative;

    double levelShift;
}
