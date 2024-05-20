package org.orient.otc.dm.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 品种筛选条件
 */
@Data
public class VarietyByTraderIdVO {


    /**
     * 品种代码
     */
    @ApiModelProperty(value = "品种代码")
    private List<String> varietyCodeList;

    /**
     * 交易员ID
     */
    @ApiModelProperty(value = "交易员ID")
    private Integer traderId;


    /**
     * 交易员名称
     */
    @ApiModelProperty(value = "交易员名称")
    private String traderName;
}
