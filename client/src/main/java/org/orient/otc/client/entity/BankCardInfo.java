package org.orient.otc.client.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
@ApiModel
public class BankCardInfo extends BaseEntity implements Serializable {
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;
    @ApiModelProperty(value = "户名")
    private String accountName;
    @ApiModelProperty(value = "开户行")
    private String openBank;
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;
    @ApiModelProperty(value = "大额行号")
    private String largeBankAccount;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "用途")
    private String purpose;
    @ApiModelProperty(value = "是否有效 0:有效 1:无效")
    private int isEffective;
}