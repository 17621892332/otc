package org.orient.otc.api.dto;

import lombok.Data;
import org.orient.otc.api.enums.ReportTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 情景分析请求参数
 */
@Data
public class GeneralDTO  implements Serializable {

    /**
     * 价格变动步长及范围
     */
    private List<BigDecimal> priceList;

    /**
     * 报告类型
     */
    private ReportTypeEnum reportType;

    /**
     * Vol变动步长及范围
     */
    private List<BigDecimal> volList;

    /**
     * 变动时间间隔内的交易日
     */
    private List<String> testDate;

    /**
     * 是否固定波动率
     */
    private Boolean isFixedVol;
}
