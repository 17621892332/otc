package org.orient.otc.client.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.client.entity.Client;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
@ApiModel
public class BankCardInfoAddDto implements Serializable {
    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "客户ID不能为空")
    private Integer clientId;

    @NotBlank(message = "户名不能为空")
    @ApiModelProperty(value = "户名")
    private String accountName;
    
    @NotBlank(message = "开户行不能为空")
    @ApiModelProperty(value = "开户行")
    private String openBank;

    @NotBlank(message = "银行账号不能为空")
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

    private int isDeleted;
}
