package org.orient.otc.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 客户表
 * </p>
 * @author 孔景军
 */
@Data
public class ClientDutyVo{

    /**
     * 主键id
     */
    private Integer id;
    /**
     * 关联表client的ID字段
     */
    @ApiModelProperty("关联表client的ID字段")
    private Integer clientId;
    /**
     * 联系人姓名
     */
    @ApiModelProperty("联系人姓名")
    private String contactName;
    /**
     * 联系人类型id
     */
    @ApiModelProperty("联系人类型id")
    private String contactTypeId;
    /**
     * 联系人类型list
     */
    @ApiModelProperty("联系人类型list")
    private List<String> contactTypeIdList;
    /**
     * 证件号
     */
    @ApiModelProperty("证件号")
    private String idCardNo;
    /**
     * 电话号码
     */
    @ApiModelProperty("电话号码")
    private String phoneNumber;
    /**
     * Email
     */
    @ApiModelProperty("Email")
    private String email;
     /**
     * 是否接收相关邮件
     */
    @ApiModelProperty("是否接收相关邮件")
    private Integer isReceiveEmail;
    /**
     * 传真
     */
    @ApiModelProperty("传真")
    private String fax;
    /**
     * 证件类型
     */
    @ApiModelProperty("证件类型")
    private String idCardType;
    /**
     * 证件有效日期
     */
    @ApiModelProperty("证件有效日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate idCardDate;
    /**
     * 联系地址
     */
    @ApiModelProperty("联系地址")
    private String address;
    /**
     * 有效截止日
     */
    @ApiModelProperty("有效截止日")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate deadLine;
    /**
     * 授权到期日
     */
    @ApiModelProperty("授权到期日")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate authorizeEndDate;
    /**
     * 微信号
     */
    @ApiModelProperty("微信号")
    private String weixin;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remarks;
}
