package org.orient.otc.api.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "簿记账户组")
public class AssetunitGroupVo implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;
}
