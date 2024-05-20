package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

@Data
@ApiModel("对冲账户分页查询信息")
public class ExchangeAccountPageListDto extends BasePage {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "账户")
    private String account;

    @ApiModelProperty(value = "簿记账户ID")
    private Integer assetunitId;

}
