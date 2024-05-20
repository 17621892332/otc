package org.orient.otc.quote.dto.collateral;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("抵押品-修改dto")
public class CollateralUpdateDto {
    @NotNull(message = "ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "客户id")
    @NotNull(message = "客户不能为空")
    private Integer clientId;

    @ApiModelProperty(value = "抵押品名称")
    @NotNull(message = "抵押品不能为空")
    private Integer varietyId;

    @ApiModelProperty(value = "盯市价格")
    private BigDecimal markPrice;

    @ApiModelProperty(value = "数量")
    @NotNull(message = "数量不能为空")
    private BigDecimal quantity;

    @ApiModelProperty(value = "抵押率")
    @NotNull(message = "抵押率不能为空")
    private BigDecimal rate;

    @ApiModelProperty(value = "质押价值")
    @NotNull(message = "质押价值不能为空")
    private BigDecimal collateralPrice;

    @ApiModelProperty(value = "抵押时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "抵押时间不能为空")
    private LocalDateTime collateralTime;

    @ApiModelProperty(value = "仓单号")
    private String number;

    @ApiModelProperty(value = "备注")
    private String remark;

}
