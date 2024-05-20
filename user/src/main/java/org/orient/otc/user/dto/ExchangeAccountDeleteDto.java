package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("删除对冲账户信息")
public class ExchangeAccountDeleteDto {

    @ApiModelProperty(value = "id")
    private Integer id;
}
