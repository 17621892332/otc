package org.orient.otc.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.client.entity.Client;
import org.orient.otc.common.core.util.FieldAlias;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chengqiang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel
public class BankCardInfoVO implements Serializable {
    private Integer id;
    @FieldAlias(value = "客户ID")
    private Integer clientId;
    @FieldAlias(value = "户名")
    private String accountName;
    @FieldAlias(value = "客户名称")
    private Client client;
    @FieldAlias(value = "开户行")
    private String openBank;
    @FieldAlias(value = "银行账号")
    private String bankAccount;
    @FieldAlias(value = "大额行号")
    private String largeBankAccount;
    @FieldAlias(value = "备注")
    private String remark;
    @FieldAlias(value = "用途")
    private String purpose;
    @FieldAlias(value = "是否有效 0:有效 1:无效")
    private int isEffective;
    private Integer creatorId;
    private Integer updatorId;
    private String updatorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
