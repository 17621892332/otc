package org.orient.otc.quote.dto.scenario;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 情景分析请求参数
 */
@Data
public class ScenarioTemplateAddDTO {

    private Integer id;

    @ApiModelProperty(value = "名称",required = true)
    @NotNull(message = "名称不能为空")
    private String name;
    /**
     * 价格上限
     */
    private BigDecimal upPrice;
    /**
     * 价格下限
     */
    private BigDecimal downPrice;
    /**
     * 价格间隔
     */
    private BigDecimal intervalPrice;

    /**
     * 波动率上限
     */
    private BigDecimal upVol;
    /**
     * 波动率下限
     */
    private BigDecimal downVol;
    /**
     * 波动率间隔
     */
    private BigDecimal intervalVol;
    /**
     * 观察天数(工作日)
     */
    @Max(value = 180,message = "观察天数不能大于180天")
    @Min(value = 1,message = "观察天数必须大于0")
    private Integer dayCount;
    /**
     * 波动率间隔
     */
    private Integer intervalDate;

    /**
     * 报告类型
     */
    @ApiModelProperty(value = "报告类型",required = true)
    @NotNull(message = "报告类型不能为空")
    private String reportType;

    /**
     * 是否客户方向
     */
    private Boolean isClient;

    /**
     * 是否固定波动率
     */
    private Boolean isFixedVol;
}
