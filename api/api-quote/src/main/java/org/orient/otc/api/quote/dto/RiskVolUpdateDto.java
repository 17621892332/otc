package org.orient.otc.api.quote.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RiskVolUpdateDto {

    private String tradeCode;

    private BigDecimal riskVol;
}
