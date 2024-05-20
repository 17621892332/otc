package org.orient.otc.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class MailTemplateUpdateDto extends BaseEntity implements Serializable {
    @NotNull(message = "ID不能为空")
    private Integer id;
    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    private String templateName;
    /**
     * 邮件标题
     */
    @NotBlank(message = "邮件标题不能为空")
    private String mailTitle;
    /**
     * 邮件内容
     */
    @NotBlank(message = "邮件内容不能为空")
    private String mailContent;

    /**
     * 追保正文
     */
    @NotBlank(message = "追保正文不能为空")
    private String appendContent;
}
