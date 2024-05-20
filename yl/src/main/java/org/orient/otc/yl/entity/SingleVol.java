package org.orient.otc.yl.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dzrh
 */
@Data
public class SingleVol {
    /**
     * 执行价格
     */
    BigDecimal strike;
    /**
     * 期限
     */
    String expire;
    /**
     * 波动率
     */
    BigDecimal vol;

}
