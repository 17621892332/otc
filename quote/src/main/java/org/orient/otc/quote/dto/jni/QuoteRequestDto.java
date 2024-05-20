package org.orient.otc.quote.dto.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.quote.dto.jni.SoRequestDto;

import javax.validation.constraints.NotNull;
import java.util.List;
@Data
@ApiModel
public class QuoteRequestDto{
    @ApiModelProperty(value = "参数list",required = true)
    @NotNull
    List<SoRequestDto> requestDtoList;

}
