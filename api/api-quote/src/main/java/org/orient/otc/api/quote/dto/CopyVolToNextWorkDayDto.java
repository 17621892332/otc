package org.orient.otc.api.quote.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
public class CopyVolToNextWorkDayDto {
    /**
     * 当前工作日
     */
    @NotNull(message = "当前工作日不能为空")
    LocalDate today;
    /**
     * 下一个工作日
     */
    @NotNull(message = "下一个工作日")
    LocalDate nextWorkDay;
}
