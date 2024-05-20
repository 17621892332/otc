package org.orient.otc.yl.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 远期
 * @author dzrh
 */
@Data
public class ForwardOption {

    /*
    *{
	"id": 11974,
	"TradeId": 11974,
	"OpenCommission": 0.0,
	"AnnualMarginRate": 0.0,
	"AnnualStoragePrice": 0.0,
	"ForwardValue": 0.0,
	"ObservationStart": null,
	"ObservationDates": null,
	"OptId": 35,
	"OptName": "刘启微",
	"OptDate": "2023-05-29T15:13:36",
	"EncryptId": "iSAZLAX3oQSqVMYaNFNm_Q"
     }*/
    /**
     * 不晓得啥含义
     */

    private BigDecimal openCommission;
    /**
     * 不晓得啥含义
     */
    private String annualMarginRate;
    /**
     * 不晓得啥含义
     */
    private String annualStoragePrice;
    /**
     * 远期价值
     */
    private BigDecimal forwardValue;

}
