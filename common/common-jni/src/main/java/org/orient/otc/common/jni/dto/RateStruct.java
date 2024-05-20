package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dzrh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateStruct {
    /**
     * 收益率，可以是返息率、红利票息率等
     */
    double rateValue;

    /**
     * 收益率是否年化
     */
    boolean rateAnnulized;
}
