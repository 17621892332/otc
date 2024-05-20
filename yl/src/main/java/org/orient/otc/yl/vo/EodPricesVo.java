package org.orient.otc.yl.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dzrh
 */
@Data
public class EodPricesVo {
    /**
     * 标的代码
     */
    String code;
    /**
     * 最高价
     */
    BigDecimal high;
    /**
     * 最低价
     */
    BigDecimal low;
    /**
     * 收盘价
     */
    BigDecimal close;
    /**
     * 结算价
     */
    BigDecimal settle;
    /**
     * 参考价
     */
    BigDecimal refer;

}
