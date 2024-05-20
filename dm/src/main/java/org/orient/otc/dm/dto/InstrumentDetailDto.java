package org.orient.otc.dm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class InstrumentDetailDto implements Serializable {
    @ApiModelProperty(value = "合约代码")
    @NotBlank(message = "合约代码不能为空")
    private String instrumentId;
}
