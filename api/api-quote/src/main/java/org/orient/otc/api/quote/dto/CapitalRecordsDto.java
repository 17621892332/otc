package org.orient.otc.api.quote.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel
public class CapitalRecordsDto implements Serializable {
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(value = "开始日期")
    private LocalDateTime startDate;

    @ApiModelProperty(value = "结束日期")
    private LocalDateTime endDate;

}
