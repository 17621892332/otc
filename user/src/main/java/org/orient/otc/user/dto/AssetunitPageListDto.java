package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

import java.util.List;

@Data
@ApiModel("薄记账户分页查询dto")
public class AssetunitPageListDto extends BasePage {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("薄记账户组ID")
    private Integer groupId;

    @ApiModelProperty("基础币种")
    private String baseCurrency;

    @ApiModelProperty("交易员ID")
    List<Integer> traderIds;
}
