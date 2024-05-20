package org.orient.otc.client.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BankAccountVo {
    @ApiModelProperty(value = "对方账号")
    private String bankAccount;
}
