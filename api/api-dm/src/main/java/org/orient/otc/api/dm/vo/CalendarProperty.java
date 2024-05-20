package org.orient.otc.api.dm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class CalendarProperty {
    @ApiModelProperty("工作日")
    private Integer workday;
    @ApiModelProperty("公共假日")
    private Integer bankHoliday;
    @ApiModelProperty("交易日")
    private Integer tradingDay;
    @ApiModelProperty("ttm")
    private BigDecimal ttm;

    // 序号在批量查询的时候使用 , 用于回填数据查找对应关系
    @ApiModelProperty(value = "序号")
    String no;
}
