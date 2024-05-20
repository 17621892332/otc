package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("新增薄记账户dto")
public class AssetunitAddDto{

    @ApiModelProperty("名称")
    @NotNull(message = "簿记账户名称不能为空")
    private String name;

    @ApiModelProperty("薄记账户组ID")
    private Integer groupId;

    @ApiModelProperty("基础币种")
    private String baseCurrency;

    @ApiModelProperty("交易员ID")
    List<Integer> traderIds;
}
