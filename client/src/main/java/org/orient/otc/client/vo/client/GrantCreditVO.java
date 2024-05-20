package org.orient.otc.client.vo.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.client.enums.GrantCreditApprovalStatusEnum;
import org.orient.otc.client.entity.ClientLevel;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class GrantCreditVO extends BaseEntity implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "客户编号")
    private String clientCode;

    @ApiModelProperty(value = "授信额度")
    private BigDecimal amount;

    @ApiModelProperty(value = "审批规模")
    private BigDecimal approvalScale;

    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "审批状态")
    private GrantCreditApprovalStatusEnum approvalStatus;
    @ApiModelProperty(value = "审批状态名称")
    private String approvalStatusName;

    //  0:给客户授信,1:给我方授信
    @ApiModelProperty(value = "方向 ( 0:给客户授信,1:给我方授信 )")
    private int direction;

    @ApiModelProperty(value = "创建人名称")
    private String creatorName;
    @ApiModelProperty(value = "修改名称")
    private String updatorName;

    @ApiModelProperty(value = "客户等级ID")
    private Integer levelId;
    @ApiModelProperty(value = "客户等级信息")
    ClientLevel clientLevel;

}
