package org.orient.otc.client.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

@Data
public class ClientPageDto extends BasePage {

    @ApiModelProperty(value = "客户编号")
    private String code;

    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "客户等级id")
    private Integer levelId;
}
