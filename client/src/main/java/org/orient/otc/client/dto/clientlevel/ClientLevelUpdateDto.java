package org.orient.otc.client.dto.clientlevel;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ClientLevelUpdateDto {

    /**
     * 客户等级id
     */
    @NotNull(message = "客户等级ID不能为空")
    Integer id;
    /**
     * 客户等级名称
     */
    String name;
    /**
     * 保证金比例
     */
    BigDecimal marginRate;
}
