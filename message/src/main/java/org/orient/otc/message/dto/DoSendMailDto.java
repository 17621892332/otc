package org.orient.otc.message.dto;

import lombok.Data;
import org.orient.otc.message.entity.MailTemplate;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 处理邮件发送入参
 * @author chengqiang
 */
@Data
public class DoSendMailDto {
    /**
     * 邮件模板
     */
    MailTemplate mailTemplate;
    /**
     * 客户ID
     */
    Integer clientId;
    /**
     * 开始日期
     */
    LocalDate startDate;
    /**
     * 查询日期(也是结束日期)
     */
    LocalDate queryDate;
    /**
     * 发送类型 -1:重发 , 0: 结算报告  ,  1:批量发送
     */
    int sendType;
    /**
     * 重发记录
     */
    MailSendRecordDetailDto mailReSendRecord;
    /**
     * 是否追保
     */
    Integer isAppendMail;
    /**
     * 报告内容
     */
    Set<String> reportTypeSet;
    /**
     * 收件人
     */
    List<String> receiveUserList;

    String authorizeInfo; // 登录信息
}
