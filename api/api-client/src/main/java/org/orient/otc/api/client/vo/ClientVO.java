package org.orient.otc.api.client.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 客户列表VO
 */
@Data
public class ClientVO {
    private Integer id;

    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String code;


    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String name;

    /**
     * 客户等级id
     */
    @ApiModelProperty(value = "客户等级id")
    private Integer levelId;

    /**
     * 客户等级名称
     */
    @ApiModelProperty(value = "客户等级名称")
    private String levelName;

    /**
     * 证照编号
     */
    private String licenseCode;
    /**
     * 商品类签署日期
     */
    private LocalDate protocolSignDate;

    /**
     * 协议签署版本
     */
    private String protocolSignVersion;

    /**
     * 是否内部客户 0:否 1:是
     */
    @ApiModelProperty(value = "是否内部客户0:否 1:是 默认否")
    private Integer isInsided;

    /**
     * 监管客户类型
     */
    private Integer clientSuperviseType;
}
