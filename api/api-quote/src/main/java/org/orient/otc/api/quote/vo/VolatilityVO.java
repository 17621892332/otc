package org.orient.otc.api.quote.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.InterpolationMethodEnum;
import org.orient.otc.api.quote.enums.VolTypeEnum;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 波动率对象
 */
@Data
public class VolatilityVO {
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
    private VolTypeEnum volType;



    /**
     * 当前波动率表格数据
     */
    @ApiModelProperty(value = "当前波动率表格数据")
    private List<VolatilityDataVO> data;
    /**
     * 当前波动率表格数据
     */
    @ApiModelProperty(value = "当前delta波动率表格数据")
    private List<VolatityDeltaDataVO> deltaData;

    /**
     * 插值方法
     */
    @ApiModelProperty(value = "插值方法")
    @NotNull
    private InterpolationMethodEnum interpolationMethod;

}
