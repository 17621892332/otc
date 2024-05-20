package org.orient.otc.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.api.message.enums.MailTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MailSendRecordDetailDto extends BaseEntity implements Serializable {
    /**
     * 主键id
     */
    private Integer id;
    /**
     * 收件人(多个用中文逗号分隔)
     */
    private String receiveUserMailAddress;
    /**
     * 抄送人(多个用中文逗号分隔)
     */
    private String carbonCopyUserMailAddress;
    /**
     * 邮件类型
     */
    private MailTypeEnum mailType;
    /**
     * 邮件发送状态 (-1: 发送中 0:成功 , 1:失败)
     */
    private Integer sendStatus;
    /**
     * 邮件发送失败的描述
     */
    private String sendFailDesc;
    /**
     * 重发次数
     * 重发次数>3 在不准重发
     */
    private Integer reSendCount;
    /**
     * 邮件ID
     */
    private String messageId;
    /**
     * 邮件主题
     */
    private String title;
    /**
     * 邮箱内容
     */
    private String emailContent;
    /**
     * 邮件重发请求参数(包含结算报的开始,结束日期,客户,报告内容枚举,多个枚举值,用中文逗号分割)
     */
    private String reSendParam;

}
