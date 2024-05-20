package org.orient.otc.quote.vo.quote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class QuotationVO {

    /**
     * 产业链
     */
    private List<QuotationDataVO> dataList;
    /**
     * 报价日期
     */
    @ApiModelProperty(value = "报价日期")
    private LocalDate quotationDate;

    /**
     * 到期日
     */
    @ApiModelProperty(value = "到期日")
    private LocalDate maturityDate;

}
