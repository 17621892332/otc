package org.orient.otc.dm.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.dm.entity.Calendar;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class CalendarDetailVo implements Serializable {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "日期")
    private LocalDate date;

    @ApiModelProperty(value = "是否是假日[工作日:weekday，假日:holiday]")
    private String isHoliday;

    @ApiModelProperty(value = "是否是交易日[交易日：tradingday，非交易日：nontradingday]")
    private String isTradingDay;

    @ApiModelProperty(value = "是否是周末[非周末:noweekend,周末:weekend]")
    private String isWeekend;
}
