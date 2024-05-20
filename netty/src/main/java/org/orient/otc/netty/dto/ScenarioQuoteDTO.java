package org.orient.otc.netty.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.enums.ReportTypeEnum;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * 情景分析请求参数
 */
@Data
public class ScenarioQuoteDTO {

    /**
     * 价格上限
     */
    @DecimalMax(value = "50",message = "价格上限必须小于50")
    private BigDecimal upPrice;
    /**
     * 价格下限
     */
    @DecimalMin(value = "-50",message = "价格下限必须大于-50")
    private BigDecimal downPrice;
    /**
     * 价格间隔
     */
    @DecimalMin(value = "0.00001",message = "价格间隔必须大于0")
    private BigDecimal intervalPrice;

    /**
     * 波动率上限
     */
    @DecimalMax(value = "10",message = "波动率上限必须小于10")
    private BigDecimal upVol;
    /**
     * 波动率下限
     */
    @DecimalMin(value = "-10",message = "波动率下限必须大于-10")
    private BigDecimal downVol;
    /**
     * 波动率间隔
     */
    @DecimalMin(value = "0.00001",message = "波动率间隔必须大于0")
    private BigDecimal intervalVol;

    /**
     * 观察天数(工作日)
     */
    @Max(value = 180,message = "观察天数不能大于180天")
    @Min(value = 1,message = "观察天数必须大于0")
    private Integer dayCount;

    /**
     * 日期间隔
     */
    @Min(value = 1,message = "日期间隔必须大于0")
    private Integer intervalDate;


    /**
     * 计算时间
     */
    @ApiModelProperty(value = "如果不传就是当前时间，如果传了就用这个时间")
    private LocalTime quoteTime;
    /**
     * 报告类型
     */
    @NotNull(message = "报告类型不能为空")
    private ReportTypeEnum reportType;

    /**
     * 是否客户方向
     */
    @NotNull(message = "是否客户方向不能为空")
    private Boolean isClient;

    /**
     * 是否固定波动率
     */
    @NotNull(message = "是否固定波动率不能为空")
    private Boolean isFixedVol;
    /**
     * 交易ID数据
     */
    @NotNull(message = "交易数据不能为空")
    private List<String> tradeIdList;
}
