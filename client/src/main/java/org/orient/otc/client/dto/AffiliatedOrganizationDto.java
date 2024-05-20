package org.orient.otc.client.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AffiliatedOrganizationDto {
    @ApiModelProperty(value = "客户名称")
    private String name;
}
