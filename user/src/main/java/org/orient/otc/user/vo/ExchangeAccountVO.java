package org.orient.otc.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.user.entity.Assetunit;

import java.io.Serializable;

/**
 * 对冲账户VO
 */
@Data
@ApiModel(value = "对冲账户Vo")
public class ExchangeAccountVO implements Serializable {
    private Integer id;
    /**
     * 账号名称
     */
    @ApiModelProperty("账户名称")
    private String name;

    @ApiModelProperty("场内账户")
    private String account;

    private String password;

    /**
     * 簿记信息
     */
    @ApiModelProperty("簿记信息")
    private Assetunit assetunit;
    /**
     * 登录信息
     */
    @ApiModelProperty("登录信息")
    private ExchangeAccountLoginVO loginInfo;

    @ApiModelProperty("禁用/启用 (0 : 禁用 1 : 启用)")
    private Integer status;
}
