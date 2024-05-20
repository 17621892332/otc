package org.orient.otc.quote.dto.riskearlywaring;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.OptionTypeEnum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
/**
 * 风险预警
 */
public class RiskEarlyWarningAddDto implements Serializable {

    /**
     * 风险预警信息数组
     */
    @ApiModelProperty(value = "风险预警信息数组")
    @NotNull(message = "风险预警信息数组不能为空")
    private List<RiskEarlyWarningItemDto> list;

}
