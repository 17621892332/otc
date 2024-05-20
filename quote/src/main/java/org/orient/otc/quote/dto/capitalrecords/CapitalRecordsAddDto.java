package org.orient.otc.quote.dto.capitalrecords;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CapitalRecordsAddDto {
    @ApiModelProperty(value = "金额")
    @NotNull(message = "金额不能为空")
    private BigDecimal money;

    @ApiModelProperty(value = "方向")
    @NotNull(message = "方向不能为空")
    private CapitalDirectionEnum direction;

    @ApiModelProperty(value = "归属时间")
    @NotNull(message = "归属时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate vestingDate;

    @ApiModelProperty(value = "发生归属时间")
    @NotNull(message = "发生时间不能为空")
    private LocalDateTime happenTime;

    @ApiModelProperty(value = "银行账户")
    @NotBlank(message = "银行账户不能为空")
    private String bankAccount;

    @ApiModelProperty(value = "客户id")
    @NotNull(message = "客户不能为空")
    private Integer clientId;

    @ApiModelProperty(value = "币种")
    @NotNull(message = "币种不能为空")
    private String currency;

    @ApiModelProperty(value = "备注")
    private String remark;
}
