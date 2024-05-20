package org.orient.otc.quote.dto.riskearlywaring;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.OptionTypeEnum;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
/**
 * 风险预警
 */
public class RiskEarlyWarningItemDto implements Serializable {

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

}
