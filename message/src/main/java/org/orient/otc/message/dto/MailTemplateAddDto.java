package org.orient.otc.message.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class MailTemplateAddDto extends BaseEntity implements Serializable {
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
    @NotBlank(message = "邮件内容不能为空")
    private String appendContent;
}
