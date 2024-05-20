package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("删除薄记账户dto")
public class AssetunitGroupDeleteDto {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Integer id;
}
