package org.orient.otc.api.client.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientLevelVo {
    @ApiModelProperty(value = "等级名称")
    private String name;

    @ApiModelProperty(value = "保证金比例")
    private BigDecimal marginRate;
}
