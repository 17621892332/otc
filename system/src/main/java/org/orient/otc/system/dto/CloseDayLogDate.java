package org.orient.otc.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CloseDayLogDate {
    @NotNull
    @ApiModelProperty(value = "时间",required = true)
    private LocalDate date;
}
