package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("修改薄记账户dto")
public class AssetunitUpdateDto {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("薄记账户组ID")
    private Integer groupId;

    @ApiModelProperty("基础币种")
    private String baseCurrency;

    @ApiModelProperty("交易员ID")
    List<Integer> traderIds;
}
