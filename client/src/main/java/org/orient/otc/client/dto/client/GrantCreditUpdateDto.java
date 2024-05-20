package org.orient.otc.client.dto.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel
public class GrantCreditUpdateDto extends BaseEntity implements Serializable {

    @NotNull(message = "ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "客户不能为空")
    private Integer clientId;

    @ApiModelProperty(value = "客户等级ID")
    private Integer levelId;

    @ApiModelProperty(value = "授信额度")
    @NotNull(message = "授信额度不能为空")
    private BigDecimal amount;

    @ApiModelProperty(value = "审批规模")
    private BigDecimal approvalScale;

    @ApiModelProperty(value = "开始日期")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    //  0:给客户授信,1:给我方授信
    @ApiModelProperty(value = "方向 ( 0:给客户授信,1:给我方授信 )")
    private int direction;

}
