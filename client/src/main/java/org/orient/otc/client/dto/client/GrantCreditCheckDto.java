package org.orient.otc.client.dto.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.client.enums.GrantCreditApprovalStatusEnum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("授信审批")
public class GrantCreditCheckDto implements Serializable {

    @NotNull(message = "ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "审批状态")
    @NotNull(message = "审批状态不能为空")
    private GrantCreditApprovalStatusEnum approvalStatus;
}
