package org.orient.otc.dm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
@ApiModel
public class CalendarYl extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "日历语言")
    private String country;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "非交易日数据")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> holidayJson;

    @ApiModelProperty(value = "一年天数")
    private Integer days;

    @ApiModelProperty(value = "交易天数")
    private Integer tradeDays;

}
