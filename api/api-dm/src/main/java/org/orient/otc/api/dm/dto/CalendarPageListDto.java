package org.orient.otc.api.dm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CalendarPageListDto extends BasePage {
    @ApiModelProperty(value = "年份",required = true)
    private Integer year;
}
