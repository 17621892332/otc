package org.orient.otc.dm.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.dm.enums.IsHolidayEnum;
import org.orient.otc.dm.enums.IsTradingDayEnum;
import org.orient.otc.dm.enums.IsWeekendEnum;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CalendarPageListVo implements Serializable {

    /**
     * 年份
     */
    @ApiModelProperty(value = "年份")
    private String year;

    @ApiModelProperty(value = "交易日天数")
    private Integer tradingDays;

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
