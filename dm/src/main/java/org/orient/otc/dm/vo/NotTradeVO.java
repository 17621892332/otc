package org.orient.otc.dm.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 系统非交易日
 */
@Data
public class NotTradeVO {
    /**
     * 非交易日
     */
    @ApiModelProperty("非交易日")
    private List<LocalDate> notTradeDayList;

    /**
     * 系统交易日
     */
    @ApiModelProperty("系统交易日")
    private LocalDate tradeDay;
}
