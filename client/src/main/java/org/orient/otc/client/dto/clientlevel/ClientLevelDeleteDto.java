package org.orient.otc.client.dto.clientlevel;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ClientLevelDeleteDto {
    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    Integer id;
}
