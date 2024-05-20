package org.orient.otc.message.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.message.enums.MailTypeEnum;
import org.orient.otc.common.database.vo.BaseVO;
import org.orient.otc.message.entity.MailSendRecordDetail;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class MailSendRecordVO extends BaseVO implements Serializable {
    /**
     * 主键id
     */
    private Integer id;
    /**
     * 发件人
     */
    private Integer sendUserId;
    private String sendUserName;
    /**
     * 收件人(多个用中文逗号分隔)
     */
    private String receiveUserMailAddress;
    /**
     * 抄送人(多个用中文逗号分隔)
     */
    private String carbonCopyUserMailAddress;
    /**
     * 邮件主题
     */
    String title;
    /**
     * 邮箱内容
     */
    private String emailContent;
    /**
     * 客户ID
     */
    private Integer clientId;
    private String clientName;
    /**
     * 邮件类型
     */
    private MailTypeEnum mailType;
    /**
     * 邮件发送状态 (0:成功 , 1:失败)
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
     * 邮件发送记录详情
     */
    List<MailSendRecordDetail> detailList;

}
