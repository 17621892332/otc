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
public class MailSendRecordDetail extends BaseEntity implements Serializable {
    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 邮件发送父ID
     */
    private Integer parentId;
    /**
     * 收件人(多个用中文逗号分隔)
     */
    private String receiveUserMailAddress;
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

}
