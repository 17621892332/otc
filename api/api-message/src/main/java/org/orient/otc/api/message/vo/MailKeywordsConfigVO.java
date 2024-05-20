package org.orient.otc.api.message.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MailKeywordsConfigVO implements Serializable {
    /**
     * 主键id
     */
    private Integer id;
    /**
     * 关键字
     */
    private String keyWord;
    /**
     * 关键字描述
     */
    private String keyWordDesc;
    /**
     * 关键字示例
     */
    private String example;
}
