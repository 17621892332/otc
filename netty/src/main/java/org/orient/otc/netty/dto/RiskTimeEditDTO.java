package org.orient.otc.netty.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 计算时间
 */
@Data
public class RiskTimeEditDTO {

    /**
     * 计算时间
     */
    private LocalDateTime riskTime;
}
