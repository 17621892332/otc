package org.orient.otc.api.message.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SendMailDto {
    /**
     * 收件人列表
     */
    @ApiModelProperty(value = "收件人列表")
    List<String> receiveUserList;
    /**
     * 邮件主题
     */
    @ApiModelProperty(value = "邮件主题")
    String title;
    /**
     * 邮件正文
     * 追保正文和邮件正文合并在一起的
     */
    @ApiModelProperty(value = "邮件正文")
    String content;
    /**
     * 追保正文
     */
    @ApiModelProperty(value = "追保正文")
    String appendContent;
    /**
     * 是否发送追保正文 1: 是 0: 否
     */
    Integer isAppendMail;
    /**
     * 追保金额
     */
    BigDecimal additionalPrice;
    /**
     * 抄送人列表
     */
    @ApiModelProperty(value = "抄送人列表")
    List<String> carbonCopyUserList;
    /**
    * 客户ID
     */
    private Integer clientId;
    /**
     * 邮件模板ID
     */
    private Integer mailTemplateId;
    /**
     * 邮件类型
     */
    private String mailType;
    /**
     * 附件列表
     */
    @ApiModelProperty(value = "附件列表")
    List<MultipartFile> fileList;
    /**
     * 结算报告附件
     */
    byte[] settlementReportTempFileByte;
    /**
     * 报告文件名称 , 仅在结算报告页面发送邮件使用此参数
     */
    private String tempFileName;
    /**
     * 重发参数, 此参数用于添加邮件记录 , 预防邮件发送失败,再次重发, 记录请求参数
     */
    private String reSendParams;

    /**
     * 发送状态:是否成功
     */
    int sendStatus;
    /**
     * 失败原因
     */
    String sendFailDesc;
    /**
     * 是否重发
     * 用于邮件发送成功之后, 是否添加邮件发送记录控制
     */
    private Boolean reSend;
    /**
     * 重发的记录ID
     */
    private Integer reSendMailId;

    String authorizeInfo; // 登录信息

    // 邮箱校验错误时(部分错误), 返回插入的主表记录ID
    Integer errorMailRecordId;
}
