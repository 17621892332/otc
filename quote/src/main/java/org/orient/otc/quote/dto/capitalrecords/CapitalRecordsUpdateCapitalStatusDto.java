package org.orient.otc.quote.dto.capitalrecords;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.CapitalStatusEnum;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CapitalRecordsUpdateCapitalStatusDto {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private List<Integer> idList;
    @ApiModelProperty(value = "资金状态")
    @NotNull(message = "资金状态不能为空")
    private CapitalStatusEnum capitalStatus;
    private String isTransDetail;
}
