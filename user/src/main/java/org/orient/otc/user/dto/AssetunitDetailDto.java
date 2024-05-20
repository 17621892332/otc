package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("获取薄记账户详情dto")
public class AssetunitDetailDto {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Integer id;
}
