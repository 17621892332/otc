package org.orient.otc.dm.dto;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class InstrumentPullDTO {
    /**
     * 用户代码
     */
    private String userId;
    /**
     * 合约代码
     */
    private String instrumentId;
    /**
     * 交易所代码
     */
    private String exchangeId;
    /**
     * 产品代码
     */
    private String productId;

}
