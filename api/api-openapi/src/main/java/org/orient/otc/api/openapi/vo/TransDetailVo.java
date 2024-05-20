package org.orient.otc.api.openapi.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * (TransDetail)表实体类
 *
 * @author szl
 * @since 2024-04-15 16:33:05
 */
@Data
public class TransDetailVo implements Serializable {

    /**
     * 主键
     */
    private String id;
    /**
     * 交易明细编号
     */
    private String billno;
    /**
     * 交易日期
     */
    private LocalDateTime bizdate;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 金额折本位币
     */
    private BigDecimal locamt;
    /**
     * 汇率
     */
    private BigDecimal exchangerate;
    /**
     * 付款金额
     */
    private BigDecimal debitamount;
    /**
     * 收款金额
     */
    private BigDecimal creditamount;
    /**
     * 余额
     */
    private BigDecimal transbalance;
    /**
     * 对方户名
     */
    private String oppunit;
    /**
     * 对方开户行
     */
    private String oppbank;
    /**
     * 对方账号
     */
    private String oppbanknumber;
    /**
     * 对账标识码
     */
    private String bankcheckflag;
    /**
     * 明细流水号
     */
    private String detailid;
    /**
     * 是否退票
     */
    private Integer isrefund;
    /**
     * 是否接收
     */
    private Integer isreced;
    /**
     * 入账状态 [3:已入账, 0:待入账]
     */
    private Integer receredtype;
    /**
     * 记账日期
     */
    private LocalDate transdate;
    /**
     * 银行账号.银行账号
     */
    private String accountbankBankaccountnumber;
    /**
     * 开户行.名称
     */
    private String accountbankBankName;
    /**
     * 发生归属时间
     */
    private LocalDateTime createtime;
}

