package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("对冲账户详情查询信息")
public class ExchangeAccountDetailDto{

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Integer id;

}
