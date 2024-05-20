package org.orient.otc.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class MailTemplate extends BaseEntity implements Serializable {
    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 邮件标题
     */
    private String mailTitle;
    /**
     * 邮件内容
     */
    private String mailContent;
    /**
     * 追保正文
     */
    private String appendContent;
    /**
     * 是否为默认模板0 : 否, 1: 是
     */
    private Integer defaultFlag;
}
