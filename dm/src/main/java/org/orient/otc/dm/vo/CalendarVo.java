package org.orient.otc.dm.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.dm.entity.Calendar;

import java.io.Serializable;
import java.util.List;

@Data
public class CalendarVo implements Serializable {

    /**
     * 年份
     */
    @ApiModelProperty(value = "年份")
    private Integer year;

    List<CalendarDetailVo> calendarList;
}
