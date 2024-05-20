package org.orient.otc.quote.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TradeRiskInfoPvDTO extends BasePage {

    /**
     * 结算日期
     */
    @ApiModelProperty("结算日期")
    private LocalDate riskDate;

    /**
     * 交易编号
     */
    @ApiModelProperty("交易编号")
    private String tradeCode;

}
