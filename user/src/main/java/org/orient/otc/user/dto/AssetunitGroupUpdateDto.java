package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("修改薄记账户dto")
public class AssetunitGroupUpdateDto {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;
}
