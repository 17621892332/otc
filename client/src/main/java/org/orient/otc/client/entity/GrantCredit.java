package org.orient.otc.client.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.client.enums.GrantCreditApprovalStatusEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 授信表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class GrantCredit  extends BaseEntity implements Serializable {
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    /**
     * 客户等级ID
     */
    @ApiModelProperty(value = "客户等级ID")
    private Integer levelId;
    /**
     * 授信额度
     */
    @ApiModelProperty(value = "授信额度")
    private BigDecimal amount;

    /**
     * 审批规模
     */
    @ApiModelProperty(value = "审批规模")
    private BigDecimal approvalScale;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 审批状态
     */
    @ApiModelProperty(value = "审批状态")
    private GrantCreditApprovalStatusEnum approvalStatus;

    /**
     * 0:给客户授信,1:给我方授信
     */
    @ApiModelProperty(value = "方向 0:给客户授信,1:给我方授信 ")
    private int direction;

}
