package org.orient.otc.api.dm.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InstrumentInfoVo {
    /**
     * 合约代码
     */
    private String instrumentId;

    /**
     * 合约名称
     */
    private String instrumentName;

    /**
     * 交易所代码
     */
    private String exchangeId;

    /**
     * 产品代码
     */
    private String productId;

    /**
     * 产品类型
     */
    private int productClass;

    /**
     * 最小价格变动单位
     */
    private Double priceTick;

    /**
     * 合约乘数
     */
    private Integer volumeMultiple;

    /**
     * 到期日期,格式: yyyyMMdd
     */
    private String expireDate;

    /**
     * 行权价
     */
    private BigDecimal strikePrice;

    /**
     * 期权类型
     */
    private int optionsType;

    /**
     * 基础商品代码
     */
    private String underlyingInstrId;
}
