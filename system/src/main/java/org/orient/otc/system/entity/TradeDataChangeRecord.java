package org.orient.otc.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.system.enums.SuccessStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易数据变更记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class TradeDataChangeRecord extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id ;
    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode ;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId ;
    /**
     * 簿记账户ID
     */
    @ApiModelProperty(value = "簿记账户ID")
    private Integer assetunitId ;
    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    private DataChangeTypeEnum changeType ;
    /**
     * 变更字段json字符串(应该是一个json数组)
     */
    @ApiModelProperty(value = "变更字段")
    private String changeFields ;
    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    private TradeStateEnum tradeState;
    /**
     * 交易日期
     */
    @ApiModelProperty(value = "交易日期")
    private LocalDate tradeDate;
    /**
     * 到期日期
     */
    @ApiModelProperty(value = "到期日期")
    private LocalDate maturityDate;
    /**
     * 标的代码
     */
    @ApiModelProperty(value = "标的代码")
    private String underlyingCode;
    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型")
    private OptionTypeEnum optionType;
    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    private BigDecimal notionalPrincipal;

    /**
     * 交易方向:  买入|卖出
     */
    @ApiModelProperty(value = "交易方向")
    private BuyOrSellEnum buyOrSell;
}
