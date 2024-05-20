package org.orient.otc.dm.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExchangeIdDto {

    @NotNull(message = "ID不能为空")
    private Integer id;
}
