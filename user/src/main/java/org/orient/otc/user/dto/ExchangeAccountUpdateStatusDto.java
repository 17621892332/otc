package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("禁用/启用对冲账户信息")
public class ExchangeAccountUpdateStatusDto {
    @NotNull(message = "id不能为空")
    private Integer id;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
