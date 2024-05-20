package org.orient.otc.quote.vo.settlementreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 资金监控
 * @author dzrh
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CapitalMonitorVO extends AccountOverviewVO {
    /**
     * 客户ID
     */
    private Integer clientId;
    /**
     * 客户编号
     */
    private String clientCode;

    private String clientName;

    private Integer clientLevelId;

    private String clientLevelName;

    /**
     * 是否内部客户
     */
    @ApiModelProperty("是否内部客户")
    private Integer isInsided;

    /**
     * 监管客户类型
     */
    @ApiModelProperty("监管客户类型")
    private Integer clientSuperviseType;
}
