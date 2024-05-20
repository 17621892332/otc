package org.orient.otc.client.dto.client;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("授信删除")
public class GrantCreditDeleteDto implements Serializable {

    @NotNull(message = "ID不能为空")
    private Integer id;
}
