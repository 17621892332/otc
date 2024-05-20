package org.orient.otc.dm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryInstrumentPage {
    @NotNull(message = "每页条数不能为空")
    @ApiModelProperty(value = "每页条数",required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "第几页",required = true)
    private Integer pageNo;
}
