package org.orient.otc.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.vo.BaseVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易数据变更记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TradeDataChangeRecordVO extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
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
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String clientName ;
    /**
     * 操作人名称
     */
    @ApiModelProperty(value = "操作人名称")
    private String creatorName ;
    /**
     * 簿记账户ID
     */
    @ApiModelProperty(value = "簿记账户ID")
    private Integer assetunitId ;
    /**
     * 簿记账户名称
     */
    @ApiModelProperty(value = "簿记账户名称")
    private String assetunitName;
    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    private DataChangeTypeEnum changeType ;
    /**
     * 变更字段
     */
    @ApiModelProperty(value = "变更字段")
    private String changeFields ;
    /**
     * 变更字段lisi
     */
    @ApiModelProperty(value = "变更字段list")
    private List<DiffObjectVO> changeFieldObjectList ;
    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    private String tradeState;

    /**
     * 所有变更的key
     */
    @ApiModelProperty(value = "变更的key")
    private String changeKey;

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
    private String optionType;
    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    private BigDecimal notionalPrincipal;

    /**
     * 交易方向:  买入|卖出
     */
    @ApiModelProperty(value = "交易方向")
    private String buyOrSell;


}
