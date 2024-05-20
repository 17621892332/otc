package org.orient.otc.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 邮件关键字配置修改dto
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MailKeywordsConfigUpdateDto implements Serializable {
    @NotNull(message = "ID不能为空")
    private Integer id;
    /**
     * 关键字
     */
    @NotBlank(message = "邮件通配符关键字不能为空")
    private String keyWord;
    /**
     * 关键字描述
     */
    @NotBlank(message = "邮件通配符描述不能为空")
    private String keyWordDesc;
    /**
     * 关键字示例
     */
    private String example;
}
