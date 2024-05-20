package org.orient.otc.dm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExchangeSaveDto {

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("交易所代码")
    private String code;

    @ApiModelProperty("交易所名称")
    private String name;

    @ApiModelProperty("交易所中文简称")
    private String shortname;
}
