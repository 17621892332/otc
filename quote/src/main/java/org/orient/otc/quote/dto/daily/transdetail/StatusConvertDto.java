package org.orient.otc.quote.dto.daily.transdetail;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class StatusConvertDto{
    @ApiModelProperty("originalId")
    @NotNull(message = "id不能为空")
    private String originalId;
    @ApiModelProperty(value = "归属时间")
    @NotNull(message = "归属时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate vestingDate;
}
