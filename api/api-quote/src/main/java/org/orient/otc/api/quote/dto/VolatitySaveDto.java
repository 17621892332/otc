package org.orient.otc.api.quote.dto;

import lombok.Data;
import org.orient.otc.api.quote.enums.InterpolationMethodEnum;
import org.orient.otc.api.quote.enums.VolTypeEnum;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
public class VolatitySaveDto {

    /**
     * 波动率类型
     */
    VolTypeEnum volType;
    /**
     * 标的代码
     */
    String underlyingCode;
    /**
     * 报价日期
     */
    private LocalDate quotationDate;
    /**
     * 插值方法
     */
    private InterpolationMethodEnum interpolationMethod;
    /**
     * 波动率曲面
     */
    List<VolatityDataDto> data;

}
