package org.orient.otc.client.vo.client;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.util.FieldAlias;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 客户表
 * </p>
 * @author 孔景军
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class ClientDutyVO extends BaseEntity implements Serializable {
    private Integer id;
    /**
     * 关联表client的ID字段
     */
    @FieldAlias("客户ID")
    private Integer clientId;
    /**
     * 联系人姓名
     */
    @FieldAlias("联系人姓名")
    private String contactName;
    /**
     * 联系人类型id
     */
    @FieldAlias("联系人类型id")
    private String contactTypeId;
    /**
     * 证件号
     */
    @FieldAlias("证件号")
    private String idCardNo;
    /**
     * 电话号码
     */
    @FieldAlias("电话号码")
    private String phoneNumber;
    /**
     * Email
     */
    @FieldAlias("Email")
    private String email;
     /**
     * 是否接收相关邮件
     */
    @FieldAlias("是否接收相关邮件")
    private Integer isReceiveEmail;
    /**
     * 传真
     */
    @FieldAlias("传真")
    private String fax;
    /**
     * 证件类型
     */
    @FieldAlias("证件类型")
    private Integer idCardType;
    /**
     * 证件有效日期
     */
    @FieldAlias("证件有效日期")
    private LocalDate idCardDate;
    /**
     * 联系地址
     */
    @FieldAlias("联系地址")
    private String address;
    /**
     * 有效截止日
     */
    @FieldAlias("有效截止日")
    private LocalDate deadLine;
    /**
     * 授权到期日
     */
    @FieldAlias("授权到期日")
    private LocalDate authorizeEndDate;
    /**
     * 微信号
     */
    @FieldAlias("微信号")
    private String weixin;
    /**
     * 备注
     */
    @FieldAlias("备注")
    private String remarks;
}
