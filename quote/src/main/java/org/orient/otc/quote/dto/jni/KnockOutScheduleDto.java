package org.orient.otc.quote.dto.jni;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class KnockOutScheduleDto {

    @ApiModelProperty(value = "敲出观察日期对应的时间戳",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime observeDate;


    @ApiModelProperty(value = "该观察日对应的敲出障碍价格",required = true)
    @NotNull
    double barrier;


    @ApiModelProperty(value = "是否为相对障碍价格",required = true)
    @NotNull
    Boolean barrierRelative;


    @ApiModelProperty(value = "该观察日对应的敲出票息",required = true)
    @NotNull
    double rebateRate;


    @ApiModelProperty(value = "敲出票息是否年化",required = true)
    @NotNull
    Boolean rebateRateAnnulized;
}
