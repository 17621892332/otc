package org.orient.otc.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class MailTemplateDeleteDto implements Serializable {
    @NotNull(message = "ID不能为空")
    private Integer id;
}
