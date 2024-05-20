package org.orient.otc.api.market.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

/**
 *  获取收盘价参数
 */
@Data
public class CloseDatePriceByDateDto {
    @NotNull(message = "合约代码不能为空")
    private Set<String> underlyingCodes;
    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    private LocalDate date;
}
