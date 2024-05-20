package org.orient.otc.common.jni.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orient.otc.common.jni.dto.VolSurface;

/**
 * 波动率曲面转换
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIDeltaVol2StrikeVolResult {
    VolSurface volSurface;

    String message;

}
