package org.orient.otc.quote.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.quote.enums.CapitalStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel
public class CapitalRecordsImportVO implements Serializable {
    /**
     * 归属时间
     */
    @ApiModelProperty(value = "归属时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate vestingDate;
    /**
     * 发生时间
     */
    private LocalDateTime happenTime;
    /**
     * 资金编号
     */
    @ApiModelProperty(value = "资金编号")
    private String capitalCode;
    /**
     * 客户id
     */
    private Integer clientId;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    /**
     * 方向类型
     */
    @ApiModelProperty(value = "方向类型")
    private CapitalDirectionEnum direction;
    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private BigDecimal money;
    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;
    /**
     * 相关交易
     */
    @ApiModelProperty(value = "相关交易")
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
    @ApiModelProperty(value = "操作人")
    private String updatorName;
    @ApiModelProperty(value = "创建人")
    private String creatorName;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "操作时间")
    private LocalDateTime updateTime;
    /**
     * 行号 , 在错误提示中使用
     */
    int rowIndex;
}
