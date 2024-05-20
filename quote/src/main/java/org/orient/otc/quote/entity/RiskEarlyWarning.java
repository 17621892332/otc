package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.CollateralEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
/**
 * 风险预警
 */
public class RiskEarlyWarning extends BaseEntity implements Serializable {
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 交易员ID
     */
    @ApiModelProperty(value = "交易员ID")
    private Integer traderId;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;
    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型")
    private String optionType;

    /**
     * 标的代码
     */
    @ApiModelProperty(value = "标的代码")
    private String underlyingCode;
    /**
     * 预警详情
     */
    @ApiModelProperty(value = "预警详情")
    private String warningText;
    /**
     * 预警类型
     */
    @ApiModelProperty(value = "预警类型")
    private String type;
    /**
     * 预警类型 0 : 未读(默认) 1: 已读
     */
    @ApiModelProperty(value = "预警状态")
    private int waringStatus;
    /**
     * 预警时间
     */
    @ApiModelProperty(value = "预警时间")
    private LocalDateTime waringTime;

}
