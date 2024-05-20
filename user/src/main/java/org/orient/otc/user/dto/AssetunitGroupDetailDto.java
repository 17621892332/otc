package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("薄记账户组详情查询dto")
public class AssetunitGroupDetailDto  {

    @ApiModelProperty(value="id")
    @NotNull(message="id不能为空")
    private Integer id;
}
