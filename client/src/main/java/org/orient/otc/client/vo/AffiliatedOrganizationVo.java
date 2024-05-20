package org.orient.otc.client.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AffiliatedOrganizationVo {
    @ApiModelProperty(value = "客户编号")
    private String id;

    @ApiModelProperty(value = "客户名称")
    private String name;
}
