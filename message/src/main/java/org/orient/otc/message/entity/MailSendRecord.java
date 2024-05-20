package org.orient.otc.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.api.message.enums.MailTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class MailSendRecord extends BaseEntity implements Serializable {
    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 发件人
     */
    private Integer sendUserId;

    /**
     * 邮件主题
     */
    private String title;
    /**
     * 邮箱内容
     */
    private String emailContent;
    /**
     * 客户ID
     */
    private Integer clientId;
    /**
     * 邮件类型
     */
    private MailTypeEnum mailType;
    /**
     * 邮件发送状态 (-1: 发送中 0:成功 , 1:失败  2: 部分成功)
     */
    private Integer sendStatus;

    /**
     * 邮件重发请求参数(包含结算报的开始,结束日期,客户,报告内容枚举,多个枚举值,用中文逗号分割)
     */
    private String reSendParam;
}
