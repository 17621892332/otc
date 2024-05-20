package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("权限删除信息")
public class PermissionDeleteDto {

    @ApiModelProperty(value = "权限ID")
    private Integer id;

}
