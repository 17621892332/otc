package org.orient.otc.client.vo.client;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.core.util.FieldAlias;

import java.io.Serializable;

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
}
