package org.orient.otc.yl.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 客户出入金入参
 */
@Data
@Builder
public class ClientCashInCashOutDTO {
    /**
     * 客户ID
     */
    private Integer clientId;
    /**
     * 出入金方向（出金，入金）
     */
    private String direction;
    /**
     * 金额
     */
    private BigDecimal money;
    /**
     * 银行账号
     */
    private String openBankCard;
    /**
     * 发生时间，格式：yyyy-MM-dd
     */
    private LocalDate happenDate;
    /**
     * 备注
     */
    private String  comments;
    /**
     * 资金标识(21:调入,121:调出)  否	(2021-01-29新增)
     */
    private Integer  cashFlag;

}
