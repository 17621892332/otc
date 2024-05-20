package org.orient.otc.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 保证金计算参数
 */
@Data
public class MarginQuoteDTO {

    private List<MarginDTO> data;

    /**
     * 保证金计算参数
     */
    @Data
    public static class MarginDTO {
        /**
         * 客户ID
         */
        private  Integer clientId;

        /**
         * 保证金系数
         */
        private BigDecimal marginRate;


        /**
         * 交易数据
         */
        private List<MarinTradeDataDTO> tradeData;
    }
}
