package org.orient.otc.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DictionaryQueryDto {
    @ApiModelProperty( value = "字典类型code", required = true)
    private String dicTypeCode;
}
