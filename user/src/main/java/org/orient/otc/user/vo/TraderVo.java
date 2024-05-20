package org.orient.otc.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "交易员")
public class TraderVo {
    /**
     * 交易员ID
     */
    @ApiModelProperty(value = "用户id")
    private Integer id;

    /**
     * 交易员名称
     */
    @ApiModelProperty(value = "用户名称")
    private String name;
    /**
     * 簿记ID列表
     */
    @ApiModelProperty(value = "簿记ID列表")
    private List<Integer> assetunitList;
}
