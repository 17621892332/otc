package org.orient.otc.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * 计算结果
 */
@Data
public class BucketedVegaVO {
    /**
     * 合约代码
     */
    private String code;
    /**
     * 计算结果
     */
    private List<BucketedVegaData> vegaTable;

    /**
     * 计算结果
     */
    @Data
    private static class BucketedVegaData {
        /**
         * Vega值
         */
        private BigDecimal vegaValue;
        /**
         * 到期时间
         */
        private Integer expire;
        /**
         * 价值
         */
        private BigDecimal strike;
        /**
         * 是否显示
         */
        private Boolean isShown;

    }
}
