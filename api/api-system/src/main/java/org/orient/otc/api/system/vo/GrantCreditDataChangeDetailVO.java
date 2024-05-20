package org.orient.otc.api.system.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.util.FieldAlias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class GrantCreditDataChangeDetailVO implements Serializable {
    @FieldAlias(value = "授信ID")
    private Integer id;

    /**
     * 客户ID
     */
    @FieldAlias(value = "客户ID")
    private Integer clientId;

    /**
     * 客户等级ID
     */
    @FieldAlias(value = "客户等级ID")
    private Integer levelId;
    /**
     * 授信额度
     */
    @FieldAlias(value = "授信额度")
    private BigDecimal amount;

    /**
     * 审批规模
     */
    @FieldAlias(value = "审批规模")
    private BigDecimal approvalScale;

    /**
     * 开始日期
     */
    @FieldAlias(value = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @FieldAlias(value = "结束日期")
    private LocalDate endDate;

    /**
     * 备注
     */
    @FieldAlias(value = "备注")
    private String remark;

    /**
     * 审批状态
     */
    @FieldAlias(value = "审批状态")
    private String approvalStatus;

    /**
     * 0:给客户授信,1:给我方授信
     */
    @FieldAlias(value = "方向 0:给客户授信,1:给我方授信 ")
    private int direction;
    @FieldAlias(value = "是否删除")
    private String isDeleted;

}
