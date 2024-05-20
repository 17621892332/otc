package org.orient.otc.yl.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 亚式期权
 * @author dzrh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AsianOption {
    /**
     * 均价起算日
     */

    private LocalDateTime averagingPeriodStartDate;
    /**
     * 均价起算方式
     */
    private String payoffType;
    /**
     * 执行价类型
     */
    private String strikeType;
    /**
     * 参与率
     */
    private BigDecimal participationRate;
    /**
     * 杆杆率
     */
    private BigDecimal strikeGearingFactor;
    /**
     * PayoffTypeCn
     */
    private String payoffTypeCn;

}
