package org.orient.otc.api.dm.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel
/**
 * 批量获取日历dto
 */
public class CalendarPropertyBatchQueryDto {

    @NotEmpty(message = "请求参数不能为空")
    List<CalendarPropertyQueryDto> list;
}
