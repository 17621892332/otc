package org.orient.otc.api.quote.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资金监控
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClientCapitalMonitorVO extends AccountOverviewVO {

    /**
     * 客户编号
     */
    private String clientCode;

    private String clientName;

    private Integer clientLevelId;

    private String clientLevelName;
}
