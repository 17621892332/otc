package org.orient.otc.api.dm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CalendarAddDto {
    @ApiModelProperty(value = "年份")
    @NotNull(message = "日历年份不能为空")
    private Integer year;

    @ApiModelProperty(value = "日期")
    @NotNull(message = "日历日期不能为空")
    private LocalDate date;

    @ApiModelProperty(value = "是否是假日[工作日:weekday，假日:holiday]")
    @NotNull(message = "是否为假日字段不能为空")
    private String isHoliday;

    @ApiModelProperty(value = "是否是交易日[交易日：tradingday，非交易日：nontradingday]")
    @NotNull(message = "是否为交易日字段不能为空")
    private String isTradingDay;

    @ApiModelProperty(value = "是否是周末[非周末:noweekend,周末:weekend]")
    @NotNull(message = "是否为周末字段不能为空")
    private String isWeekend;
}
