package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

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
@EqualsAndHashCode(callSuper = true)
@TableName(autoResultMap = true)
@ApiModel
public class TransDetail extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @ApiModelProperty("原始主键")
    @TableField(value = "original_id")
    private String originalId;
    @ApiModelProperty("交易明细编号")
    private String billno;
    @ApiModelProperty("交易日期")
    private LocalDate bizdate;
    @ApiModelProperty("金额")
    private BigDecimal amount;
    @ApiModelProperty("金额折本位币")
    private BigDecimal locamt;
    @ApiModelProperty("汇率")
    private BigDecimal exchangerate;
    @ApiModelProperty("付款金额")
    private BigDecimal debitamount;
    @ApiModelProperty("收款金额")
    private BigDecimal creditamount;
    @ApiModelProperty("余额")
    private BigDecimal transbalance;
    @ApiModelProperty("对方户名")
    private String oppunit;
    @ApiModelProperty("对方开户行")
    private String oppbank;
    @ApiModelProperty("对方账号")
    private String oppbanknumber;
    @ApiModelProperty("对账标识码")
    private String bankcheckflag;
    @ApiModelProperty("明细流水号")
    private String detailid;
    @ApiModelProperty("是否退票")
    private Integer isrefund;
    @ApiModelProperty("是否接收")
    private Integer isreced;
    @ApiModelProperty("入账状态 [3:已入账, 0:待入账]")
    private Integer receredtype;
    @ApiModelProperty("记账日期")
    private LocalDate transdate;
    @ApiModelProperty("银行账号.银行账号")
    @TableField(value = "accountbank_bankaccountnumber")
    private String accountbankBankaccountnumber;
    @ApiModelProperty("开户行.名称")
    @TableField(value = "accountbank_bank_name")
    private String accountbankBankName;
    @ApiModelProperty("是否已确认")
    @TableField(value = "isconfirm")
    private Integer isconfirm;
    @ApiModelProperty("发生归属时间")
    @TableField(value = "happentime")
    private LocalDateTime happentime;
    @ApiModelProperty("资金编号")
    @TableField(value = "capitalId")
    private Integer capitalId;
}

