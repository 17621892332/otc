package org.orient.otc.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SettlementLogDate {
    @NotNull
    @ApiModelProperty(value = "时间",required = true)
    private LocalDate date;
}
