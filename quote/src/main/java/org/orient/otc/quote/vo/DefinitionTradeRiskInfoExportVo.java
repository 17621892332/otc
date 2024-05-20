package org.orient.otc.quote.vo;

import cn.hutool.core.annotation.Alias;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 自定义交易风险到处
 * @author dzrh
 */
@Data
@ApiModel("风险导出")
public class DefinitionTradeRiskInfoExportVo {
    @Alias("交易编号")
    private String tradeCode;
    @Alias("估值日期")
    private LocalDate riskDate;
    @Alias("持仓市值")
    private String availableAmount;
    @Alias("保证金")
    private String margin;
    @Alias(value = "Delta")
    private BigDecimal delta;
    @Alias(value = "Gamma")
    private BigDecimal gamma;
    @Alias("Vega")
    private BigDecimal vega;
    @Alias(value = "Theta")
    private BigDecimal theta;
    @Alias(value = "Rho")
    private BigDecimal rho;

}
