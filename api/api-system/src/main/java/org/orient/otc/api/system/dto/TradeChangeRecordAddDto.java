package org.orient.otc.api.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.api.system.enums.TradeStateEnum;
import org.orient.otc.common.core.vo.DiffObjectVO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易数据变更dto
 */
@Data
public class TradeChangeRecordAddDto {
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
     * 变更字段obj
     */
    @ApiModelProperty(value = "变更字段obj")
    private List<DiffObjectVO> changeFieldObjectList ;
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
