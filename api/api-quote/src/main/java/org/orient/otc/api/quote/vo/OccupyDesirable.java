package org.orient.otc.api.quote.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.config.BigDecimalFormatter;

import java.math.BigDecimal;


/**
 * 占用与可取
 */
@Data
public class OccupyDesirable {

    /**
     * 保证金占用
     */
    @ApiModelProperty(value = "保证金占用")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal marginOccupyPrice;

    /**
     * 可用资金
     */
    @ApiModelProperty(value = "可用资金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal availablePrice;

    /**
     * 授信额度
     */
    @ApiModelProperty(value = "授信额度")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal creditPrice;

    /**
     * 授信占用
     */
    @ApiModelProperty(value = "授信占用")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal creditOccupyPrice;

    /**
     * 追保金额
     */
    @ApiModelProperty(value = "追保金额")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal additionalPrice;

    /**
     * 可取资金
     */
    @ApiModelProperty(value = "可取资金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal desirablePrice;
}
