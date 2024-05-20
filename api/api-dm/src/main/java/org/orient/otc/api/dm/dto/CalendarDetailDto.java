package org.orient.otc.api.dm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CalendarDetailDto {
    @ApiModelProperty(value = "年份")
    @NotNull(message = "日历年份不能为空")
    private Integer year;
}
