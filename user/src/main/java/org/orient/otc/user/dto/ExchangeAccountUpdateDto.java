package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("修改对冲账户信息")
public class ExchangeAccountUpdateDto {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "账户")
    private String account;

    /**
     * 密码
     */
    private String password;

    @ApiModelProperty(value = "簿记账户ID")
    private Integer assetunitId;

}
