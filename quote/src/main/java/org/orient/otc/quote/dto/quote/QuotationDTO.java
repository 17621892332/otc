package org.orient.otc.quote.dto.quote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 报价参数
 */
@Data
public class QuotationDTO {

    /**
     * 到期日
     */
    @ApiModelProperty(value = "到期日")
    private LocalDate maturityDate;
}
