package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

@Data
@ApiModel("新增薄记账户dto")
public class AssetunitGroupAddDto {

    @ApiModelProperty("名称")
    private String name;
}
