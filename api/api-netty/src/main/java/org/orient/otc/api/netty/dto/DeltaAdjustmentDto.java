package org.orient.otc.api.netty.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeltaAdjustmentDto {

    private String  underlyingCode;

    private BigDecimal adjustment;
}
