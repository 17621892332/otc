package org.orient.otc.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.orient.otc.common.core.config.BigDecimalFormatter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 情景分析计算结果
 */
@Data
public class ScenarioQuoteVO  implements Serializable {
    /**
     * 合约代码
     */
    private String underlying;
    /**
     * 价格
     */
    @BigDecimalFormatter(newScale = 4)
    private  BigDecimal spotPrice;

    /**
     * 价格展示
     */
    private  String spotPriceShow;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private LocalDateTime spotDate;

    /**
     * 波动率
     */
    private  BigDecimal spotVol;
    /**
     * 估值
     */
    @BigDecimalFormatter(newScale = 1)
    private BigDecimal pv;

    /**
     * 累计盈亏
     */
    @BigDecimalFormatter(newScale = 1)
    private BigDecimal pnl;

    /**
     * 标的价格变化对期权价值的影响
     */
    @BigDecimalFormatter(newScale = 1)
    private BigDecimal deltaLots;


    /**
     * 标的价格变化对期权价值的影响
     */
    @BigDecimalFormatter(newScale = 1)
    private BigDecimal deltaCash;

    /**
     * 标的价格变化对Delta的影响
     */
    @BigDecimalFormatter(newScale = 1)
    private  BigDecimal gammaLots;

    /**
     * 标的价格变化对Delta的影响
     */
    @BigDecimalFormatter(newScale = 1)
    private  BigDecimal gammaCash;

    /**
     * 波动率变化对期权价值的影响
     */
    @BigDecimalFormatter(newScale = 1)
    private  BigDecimal vega;

    /**
     * 时间变化对期权价值的影响
     */
    @BigDecimalFormatter(newScale = 1)
    private BigDecimal theta;
}
