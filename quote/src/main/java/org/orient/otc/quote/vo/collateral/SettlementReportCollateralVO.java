package org.orient.otc.quote.vo.collateral;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.CollateralEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 抵押品VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class SettlementReportCollateralVO {
    private Integer id;

    @ApiModelProperty(value = "抵押品ID")
    private Integer varietyId;
    @ApiModelProperty(value = "抵押品名称")
    private String varietyName;

    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    @ApiModelProperty(value = "客户")
    private String clientName;


    @ApiModelProperty(value = "盯市价格")
    private BigDecimal markPrice;

    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "抵押率")
    private BigDecimal rate;

    @ApiModelProperty(value = "质押价值")
    private BigDecimal collateralPrice;

    @ApiModelProperty(value = "抵押时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime collateralTime;

    @ApiModelProperty(value = "抵押状态")
    private CollateralEnum.CollateralStatusEnum collateralStatus;
    @ApiModelProperty(value = "抵押状态名称")
    private String collateralStatusName;

    @ApiModelProperty(value = "仓单号")
    private String number;
    @ApiModelProperty(value = "出入金单号")
    private String capitalCode;

    @ApiModelProperty(value = "执行状态")
    private CollateralEnum.ExecuteStatusEnum executeStatus;

    @ApiModelProperty(value = "执行状态名称")
    private String executeStatusNAme;

    @ApiModelProperty(value = "赎回时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime redemptionTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人名称")
    private String creatorName;
    @ApiModelProperty(value = "修改名称")
    private String updatorName;

}
