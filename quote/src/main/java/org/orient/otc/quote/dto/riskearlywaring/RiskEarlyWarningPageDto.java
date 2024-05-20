package org.orient.otc.quote.dto.riskearlywaring;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
/**
 * 风险预警分页入参
 */
public class RiskEarlyWarningPageDto extends BasePage implements Serializable {

    /**
     * 交易员ID数组
     */
    @ApiModelProperty(value = "交易员ID数组")
    private List<Integer> traderIdList;

    /**
     * 客户ID数组
     */
    @ApiModelProperty(value = "客户ID数组")
    private List<Integer> clientIdList;
    /**
     * 期权类型数组
     */
    @ApiModelProperty(value = "期权类型数组")
    private List<String> optionTypeList;

    /**
     * 标的代码数组
     */
    @ApiModelProperty(value = "标的代码数组")
    private List<String> underlyingCodeList;
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
     * 预警时间-开始
     */
    @ApiModelProperty(value = "预警时间-开始")
    private LocalDate waringTimeStart;
    /**
     * 预警时间-结束
     */
    @ApiModelProperty(value = "预警时间-结束")
    private LocalDate waringTimeEnd;

}
