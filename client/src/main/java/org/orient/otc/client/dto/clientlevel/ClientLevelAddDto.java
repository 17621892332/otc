package org.orient.otc.client.dto.clientlevel;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ClientLevelAddDto {
    /**
     * 客户等级名称
     */
    @NotBlank(message = "客户等级名称不能为空")
    String name;
    /**
     * 保证金比例
     */
    BigDecimal marginRate;
}
