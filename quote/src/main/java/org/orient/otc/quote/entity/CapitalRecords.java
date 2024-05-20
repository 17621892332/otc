package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.quote.enums.CapitalStatusEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资金记录实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class CapitalRecords extends BaseEntity implements Serializable {
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 资金编号
     */
    @ApiModelProperty(value = "资金编号")
    private String capitalCode;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private BigDecimal money;

    /**
     * 方向
     */
    @ApiModelProperty(value = "方向")
    private CapitalDirectionEnum direction;

    /**
     * 发生时间
     */
    @ApiModelProperty(value = "发生时间")
    private LocalDateTime happenTime;

    /**
     * 归属时间
     */
    @ApiModelProperty(value = "归属时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate vestingDate;

    /**
     * 银行账户
     */
    @ApiModelProperty(value = "银行账户")
    private String bankAccount;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 相关交易ID
     */
    @ApiModelProperty(value = "相关交易ID")
    private Integer tradeId;

    /**
     * 平仓ID
     */
    @ApiModelProperty(value = "平仓ID")
    private Integer closeId;
    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;

    /**
     * 标的代码
     */
    @ApiModelProperty(value = "标的代码")
    private String underlyingCode;

    /**
     * 资金状态
     */
    @ApiModelProperty(value = "资金状态")
    private CapitalStatusEnum capitalStatus;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 镒链资金记录ID
     */
    private Integer ylId;

}
