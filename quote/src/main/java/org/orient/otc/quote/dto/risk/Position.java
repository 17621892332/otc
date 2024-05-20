package org.orient.otc.quote.dto.risk;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class Position {
    private BigDecimal shortCost;

    private BigDecimal longCost;

    private BigDecimal longYd;

    private BigDecimal longVolumn;

    private BigDecimal shortYd;

    private BigDecimal shortVolumn;

}
