package org.orient.otc.quote.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.orient.otc.common.core.config.BigDecimalFormatter;
import org.orient.otc.common.database.config.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@ApiModel("风险")
public class TradeRiskImportVo {

    /**
     * 交易编号
     */
    @ExcelProperty(value = "交易编号")
    private String tradeCode;

    /**
     * 风险日期
     */
    @ExcelProperty("风险日期")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate riskDate;


    /**
     * 存续单价
     */
    @ExcelProperty("存续单价")
    private BigDecimal availablePremium;

    /**
     * 存续总额
     */
    @ExcelProperty("存续总额")
    private BigDecimal availableAmount;

    /**
     * 持仓保证金
     */
    @ExcelProperty(value = "持仓保证金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal margin;

    @ExcelProperty(value = "delta")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal delta;

    @ExcelProperty(value = "gamma")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal gamma;

    @ExcelProperty(value = "theta")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal theta;

    @ExcelProperty(value = "vega")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal vega;

    /**
     * 无风险利率变化对期权价值的影响
     */
    @ExcelProperty(value = "rho")
    private BigDecimal rho;

    /**
     * 股息率变化对期权价值的影响
     */
    @ExcelProperty(value = "dividendRho")
    private BigDecimal dividendRho;
}
