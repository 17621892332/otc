package org.orient.otc.api.market.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MarketCodeDto {
    /**
     * 合约代码
     */
    @NotNull(message = "合约代码不能为空")
    private String underlyingCode;
}
