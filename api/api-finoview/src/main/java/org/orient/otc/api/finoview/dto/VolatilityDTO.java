package org.orient.otc.api.finoview.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 波动率对象
 */
@Data
public class VolatilityDTO {
    /**
     * 标的ID
     */
    @ApiModelProperty(value = "标的code")
    @NotNull
    private String underlyingCode;

    /**
     * 报价日期
     */
    @ApiModelProperty(value = "报价日期")
    @NotNull
    private LocalDate quotationDate;

    /**
     * 波动率类型
     */
    @ApiModelProperty(value = "波动率类型")
    @NotNull
    private String volType;



    /**
     * 当前波动率表格数据
     */
    @ApiModelProperty(value = "当前波动率表格数据")
    private List<VolatilityDataDTO> data;
    /**
     * 当前波动率表格数据
     */
    @ApiModelProperty(value = "当前delta波动率表格数据")
    private List<VolatityDeltaDataDTO> deltaData;

    /**
     * 插值方法
     */
    @ApiModelProperty(value = "插值方法")
    @NotNull
    private String interpolationMethod;

}
