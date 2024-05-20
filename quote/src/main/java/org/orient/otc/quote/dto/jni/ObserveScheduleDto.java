package org.orient.otc.quote.dto.jni;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ObserveScheduleDto {
    @ApiModelProperty(value = "采价日",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime observeDate;

    @ApiModelProperty(value = "已采集价格序列",required = true)
    @NotNull
    Double fixedPrice;
}
