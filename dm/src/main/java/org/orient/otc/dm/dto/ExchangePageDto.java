package org.orient.otc.dm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;

@Data
public class ExchangePageDto extends BasePage {

    @ApiModelProperty("交易所代码")
    private String code;

    @ApiModelProperty("交易所名称")
    private String name;

    @ApiModelProperty("交易所中文简称")
    private String shortname;
}
