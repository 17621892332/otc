package org.orient.otc.api.dm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradayAddDaysDto {
    /**
     * 日期
     */
    @ApiModelProperty(value = "日期",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @NotNull(message = "日期不能为空")
    private LocalDate date;

    /**
     * 天数
     */
    @ApiModelProperty(value = "天数",required = true)
    @NotNull(message = "天数不能为空")
    private Integer days;
}
