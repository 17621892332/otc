package org.orient.otc.quote.vo.settlementreport;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.config.BigDecimalFormatter;

import java.math.BigDecimal;


/**
 * 收支与结存
 */
@Data
public class InOutBalance {

    /**
     * 期初结存
     */
    @ApiModelProperty(value = "期初结存")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal startBalance;


    /**
     * 出金入金
     */
    @ApiModelProperty(value = "出金入金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal inOutPrice;

    /**
     * 成交收支
     */
    @ApiModelProperty(value = "成交收支")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal tradePrice;

    /**
     * 了结收支
     */
    @ApiModelProperty(value = "了结收支")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal closePrice;

    /**
     * 其他收支
     */
    @ApiModelProperty(value = "其他收支")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal otherPrice;

    /**
     * 期末结存
     */
    @ApiModelProperty(value = "期末结存")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal endBalance;

    /**
     * 质押市值
     */
    @ApiModelProperty(value = "质押市值")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal pledgePrice;
}
