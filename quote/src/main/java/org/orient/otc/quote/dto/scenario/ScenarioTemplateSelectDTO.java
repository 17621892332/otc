package org.orient.otc.quote.dto.scenario;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 情景分析请求参数
 */
@Data
public class ScenarioTemplateSelectDTO {
    @ApiModelProperty(value = "id",required = true)
    @NotNull(message = "id不能为空")
    private int id;
}
