package org.orient.otc.quote.vo.riskearlywaring;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
/**
 * 风险预警VO
 */
public class RiskEarlyWarningVO extends BaseEntity implements Serializable {
    private Integer id;

    /**
     * 交易员ID
     */
    @ApiModelProperty(value = "交易员ID")
    private Integer traderId;
    private String traderName;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;
    private String clientName;
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
     * 预警类型
     */
    @ApiModelProperty(value = "预警状态")
    private Integer waringStatus;
    /**
     * 预警时间
     */
    @ApiModelProperty(value = "预警时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime waringTime;

}
