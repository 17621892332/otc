package org.orient.otc.quote.dto.capitalrecords;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CapitalRecordsUpdateRemarkDto {
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Integer id;

    @ApiModelProperty(value = "备注")
    @NotBlank(message = "备注不能为空")
    private String remark;
}
