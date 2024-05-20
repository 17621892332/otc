package org.orient.otc.api.system.vo;

import lombok.Data;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.common.core.util.FieldAlias;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资金记录变更详情VO
 */
@Data
public class CapitalDataChangeDetailVO {
    /**
     * 资金ID
     */
    @FieldAlias(value = "资金ID")
    private Integer id;
    /**
     * 资金编号
     */
    @FieldAlias(value = "资金编号")
    private String capitalCode;

    /**
     * 金额
     */
    @FieldAlias(value = "金额")
    private BigDecimal money;

    /**
     * 方向
     */
    @FieldAlias(value = "方向")
    private CapitalDirectionEnum direction;

    /**
     * 发生时间
     */
    @FieldAlias(value = "发生时间")
    private LocalDateTime happenTime;

    /**
     * 归属时间
     */
    @FieldAlias(value = "归属时间")
    private LocalDate vestingDate;

    /**
     * 银行账户
     */
    @FieldAlias(value = "银行账户")
    private String bankAccount;

    /**
     * 客户id
     */
    @FieldAlias(value = "客户id")
    private Integer clientId;

    /**
     * 备注
     */
    @FieldAlias(value = "备注")
    private String remark;
    /**
     * 相关交易ID
     */
    @FieldAlias(value = "相关交易ID")
    private Integer tradeId;

    /**
     * 平仓ID
     */
    @FieldAlias(value = "平仓ID")
    private Integer closeId;
    /**
     * 交易编号
     */
    @FieldAlias(value = "交易编号")
    private String tradeCode;

    /**
     * 标的代码
     */
    @FieldAlias(value = "标的代码")
    private String underlyingCode;

    /**
     * 资金状态
     */
    @FieldAlias(value = "资金状态")
    private String capitalStatus;

    /**
     * 币种
     */
    @FieldAlias(value = "币种")
    private String currency;

    @FieldAlias(value = "操作时间")
    private LocalDateTime operationTime;
    /**
     * 是否删除
     */
    @FieldAlias(value = "是否删除")
    private String isDeleted;
}
