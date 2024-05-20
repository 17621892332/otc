package org.orient.otc.quote.dto.daily;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 查询日期
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DailyPageDTO extends BasePage {

    /**
     * 查询日期
     */
    @NotNull(message = "查询日期不能为空")
    LocalDate queryDate;
}
