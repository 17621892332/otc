package org.orient.otc.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.message.enums.MailTypeEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class MailSendRecordPageDto extends BasePage implements Serializable {
    /**
     * 发件人
     */
    private List<Integer> sendUserIdList;
    /**
     * 收件人(多个用中文逗号分隔)
     */
    private String receiveUserMailAddress;
    /**
     * 客户ID
     */
    private Integer clientId;
    /**
     * 邮件类型
     */
    private MailTypeEnum mailType;
    /**
     * 邮件发送状态 (0:成功 , 1:失败)
     */
    private Integer sendStatus;
    /**
     * 发送日期
     */
    private LocalDate createTime;

}
