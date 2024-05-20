package org.orient.otc.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarginVO {

    /**
     * 交易代码
     */
    private String tradeCode;

    /**
     * 保证金
     */
    private BigDecimal margin;
}
