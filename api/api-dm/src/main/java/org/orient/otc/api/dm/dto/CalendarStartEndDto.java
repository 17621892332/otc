package org.orient.otc.api.dm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel
public class CalendarStartEndDto {
    @ApiModelProperty(value = "开始日期",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    @ApiModelProperty(value = "结束日期",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
}
