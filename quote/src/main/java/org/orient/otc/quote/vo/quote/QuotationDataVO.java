package org.orient.otc.quote.vo.quote;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.config.BigDecimalFormatter;

import java.math.BigDecimal;

/**
 * 报价预览结果
 */
@Data
public class QuotationDataVO {

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private  Integer sort;

    /**
     * 产业链
     */
    @ApiModelProperty(value = "产业链")
    private String  varietyType;

    /**
     * 品种名称
     */
    @ApiModelProperty(value = "品种名称")
    private String varietyName;

    /**
     * 合约代码
     */
    @ApiModelProperty(value = "合约代码")
    private String underlyingCode;

    /**
     * 标的价格
     */
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.0")
    @ApiModelProperty(value = "标的价格")
    private BigDecimal underlyingPrice;

    /**
     * 客户买价
     */
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    @ApiModelProperty(value = "客户买价")
    private BigDecimal clientBuyPrice;

    /**
     * 客户买Vol
     */
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    @ApiModelProperty(value = "客户买Vol")
    private BigDecimal clientBuyVol;

    /**
     * 客户卖价
     */
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    @ApiModelProperty(value = "客户卖价")
    private BigDecimal clientSellPrice;

    /**
     * 客户卖Vol
     */
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    @ApiModelProperty(value = "客户卖Vol")
    private BigDecimal clientSellVol;
}
