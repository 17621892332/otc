package org.orient.otc.api.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel
public class BankCardInfoVO implements Serializable {
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
    private int isDeleted;
    private Integer creatorId;
    private Integer updatorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
