package org.orient.otc.quote.vo;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.quote.enums.CapitalStatusEnum;
import org.orient.otc.common.core.config.BigDecimalFormatter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资金记录对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class CapitalRecordsVO implements Serializable {
    private Integer id;

    @Alias(value = "操作时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime happenTime;
    @Alias(value = "归属时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate vestingDate;
    @Alias(value = "资金编号")
    private String capitalCode;
    @Alias(value = "客户名称")
    private String clientName;
    /**
     * 方向名称
     * 导出使用
     */
    @Alias(value = "方向类型")
    private String directionName;
    @Alias(value = "金额")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "###0.00")
    private BigDecimal money;
    @Alias(value = "币种")
    private String currency;
    @Alias(value = "相关交易")
    private String tradeCode;
    @Alias(value = "标的代码")
    private String underlyingCode;
    /**
     * 资金状态名称
     * 导出使用
     */
    @Alias(value = "资金状态")
    private String capitalStatusName;
    @Alias(value = "操作人")
    private String updatorName;
    @Alias(value = "创建人")
    private String creatorName;

    private CapitalDirectionEnum direction;
    @ApiModelProperty(value = "银行账户")
    private String bankAccount;
    @ApiModelProperty(value = "客户id")
    private Integer clientId;
    @ApiModelProperty(value = "客户信息")
    private ClientVO client;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "资金状态")
    private CapitalStatusEnum capitalStatus;
    private int isDeleted;
    private Integer creatorId;
    private Integer updatorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String createTimeString;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String updateTimeString;

    @ApiModelProperty(value = "操作时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;



}
