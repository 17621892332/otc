package org.orient.otc.api.dto;

import lombok.Data;

import java.util.List;

/**
 * BucketedVega请求参数
 */
@Data
public class BucketedVegaDTO {
    private List<BucketedVega> data;
    @Data
    public static class BucketedVega {
        private BucketedVegaGeneral general;

        private List<TradeDataDTO> tradeData;
        @Data
        public static class BucketedVegaGeneral {

            /**
             * 合约代码
             */
            private String  code;
            /**
             * 波动率
             */
            private List<VolatityDataDTO> volList;
        }
    }
}
