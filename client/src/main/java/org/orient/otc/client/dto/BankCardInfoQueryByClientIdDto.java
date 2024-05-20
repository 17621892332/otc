package org.orient.otc.client.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BankCardInfoQueryByClientIdDto {
    @ApiModelProperty(value = "客户id")
    @NotNull(message = "客户ID不能为空")
    private Integer clientId;

    @ApiModelProperty(value = "是否查询有效")
    private Integer isEffective;
}
