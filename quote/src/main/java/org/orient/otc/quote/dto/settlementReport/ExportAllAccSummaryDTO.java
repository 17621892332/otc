package org.orient.otc.quote.dto.settlementReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 累计汇总批量导出
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "累计汇总批量导出")
public class ExportAllAccSummaryDTO  implements Serializable {


    /**
     * 结束日期
     */
    @NotNull(message = "结束日期不能为空")
    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

}
