package org.orient.otc.dm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
@ApiModel
public class Calendar extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 年份
     */
    @ApiModelProperty(value = "年份")
    private Integer year;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private LocalDate date;

    /**
     * 是否是假日
     */
    @ApiModelProperty(value = "是否是假日")
    private IsHolidayEnum isHoliday;

    /**
     * 是否是交易日
     */
    @ApiModelProperty(value = "是否是交易日")
    private IsTradingDayEnum isTradingDay;

    /**
     * 是否是周末
     */
    @ApiModelProperty(value = "是否是周末")
    private IsWeekendEnum isWeekend;
}
