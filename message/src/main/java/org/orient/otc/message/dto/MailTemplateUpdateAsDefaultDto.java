package org.orient.otc.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class MailTemplateUpdateAsDefaultDto extends BaseEntity implements Serializable {
    @NotNull(message = "ID不能为空")
    private Integer id;
}
