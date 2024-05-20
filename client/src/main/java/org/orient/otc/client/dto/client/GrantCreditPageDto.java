package org.orient.otc.client.dto.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.client.enums.GrantCreditApprovalStatusEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("授信分页")
public class GrantCreditPageDto extends BasePage {
    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    @ApiModelProperty(value = "授信日期-开始")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate startMaturityDate;

    @ApiModelProperty(value = "授信日期-结束")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate endMaturityDate;

    @ApiModelProperty(value = "审批状态")
    private List<GrantCreditApprovalStatusEnum> approvalStatusList;

    @ApiModelProperty(value = "方向 ( 0:给客户授信,1:给我方授信 )")
    private int direction;

    @ApiModelProperty(value = "客户等级id")
    private List<Integer> levelIdList;
}
