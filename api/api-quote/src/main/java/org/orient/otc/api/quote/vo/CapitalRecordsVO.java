package org.orient.otc.api.quote.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.quote.enums.CapitalStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资金记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class CapitalRecordsVO implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "资金编号")
    private String capitalCode;
    @ApiModelProperty(value = "金额")
    private BigDecimal money;
    @ApiModelProperty(value = "方向")
    private CapitalDirectionEnum direction;
    @ApiModelProperty(value = "发生时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime happenTime;
    @ApiModelProperty(value = "归属时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate vestingDate;
    @ApiModelProperty(value = "银行账户")
    private String bankAccount;
    @ApiModelProperty(value = "客户id")
    private Integer clientId;
    @ApiModelProperty(value = "客户信息")
    private String clientName;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "相关交易")
    private String tradeCode;
    @ApiModelProperty(value = "标的代码")
    private String underlyingCode;
    @ApiModelProperty(value = "资金状态")
    private CapitalStatusEnum capitalStatus;

    private int isDeleted;
    private Integer creatorId;
    private String creatorName;
    private Integer updatorId;
    private String updatorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Integer ylId;
}
