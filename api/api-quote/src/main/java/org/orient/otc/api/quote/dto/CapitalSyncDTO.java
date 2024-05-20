package org.orient.otc.api.quote.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapitalSyncDTO {
    @ApiModelProperty(value = "id")
    @NotNull
    private Integer id;

    private Integer ylId;

    private String number;
}
