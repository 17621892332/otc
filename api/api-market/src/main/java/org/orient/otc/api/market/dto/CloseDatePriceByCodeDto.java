package org.orient.otc.api.market.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CloseDatePriceByCodeDto {
    /**
     * 合约代码
     */
    @NotNull(message = "合约代码不能为空")
    private String underlyingCode;
    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    private LocalDate date;
}
