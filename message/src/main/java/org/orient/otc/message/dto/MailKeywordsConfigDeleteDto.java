package org.orient.otc.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 删除通配符dto
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MailKeywordsConfigDeleteDto implements Serializable {
    @NotNull(message = "ID不能为空")
    private List<Integer> idList;
}
