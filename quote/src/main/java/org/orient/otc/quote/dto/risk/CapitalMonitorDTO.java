package org.orient.otc.quote.dto.risk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;
import org.orient.otc.quote.enums.CapitalMonitorConditionsEnum;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 资金监控请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CapitalMonitorDTO extends BasePage {

    /**
     * 客户ID列表
     */
    @ApiModelProperty(value = "客户ID列表")
    private Set<Integer> clientIdList;

    /**
     * 是否内部客户
     */
    @ApiModelProperty("是否内部客户")
    private Integer isInsided;


    /**
     * 监管客户类型
     */
    @ApiModelProperty("监管客户类型")
    private List<Integer> clientSuperviseTypeList;

    /**
     * 查询日期
     */
    @ApiModelProperty(value = "查看日期")
    @NotNull(message = "查询日期不能为空")
    private LocalDate queryDate;

    /**
     * 预置条件
     */
    @ApiModelProperty(value = "预置条件")
    private List<CapitalMonitorConditionsEnum> conditionsList;
}
