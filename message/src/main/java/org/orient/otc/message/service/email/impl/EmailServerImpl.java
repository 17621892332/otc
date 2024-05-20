package org.orient.otc.message.service.email.impl;

import com.alibaba.fastjson.JSON;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.message.dto.SendMailDto;
import org.orient.otc.api.quote.feign.SettlementClient;
import org.orient.otc.message.exception.BussinessException;
import org.orient.otc.message.service.email.EmailServer;
import org.orient.otc.message.service.email.MailSendRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Properties;

/**
 * 邮件服务实现
 */
@Service
@Slf4j
public class EmailServerImpl implements EmailServer {
    @Autowired
    SettlementClient settlementClient;
    @Autowired
    HttpServletResponse response;
    @Autowired
    MailSendRecordService mailSendRecordService;
    @Value("${spring.mail.host}")
    String host; // 邮件服务器的SMTP地址
    @Value("${spring.mail.port}")
    Integer port; // 端口
    @Value("${spring.mail.from}")
    String from; // 发件人邮箱
    @Value("${spring.mail.password}")
    String password; // 授权码

    /**
     * 发送邮件
     * 追保金额<=0 不发送追保正文 ,只发送普通正文
     * 追保金额大于0 , 只发送追保正文 ,不发送普通正文 , 且邮件标题前面加上"【追保通知】"
     *
     * @return 返回值
     */
    @Override
    public boolean sendEMail(SendMailDto dto) {
        log.info("发送邮件入参sendEMail="+ JSON.toJSONString(dto));
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", Integer.toString(port));
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.transport.protocol", "smtp");
            // 企业邮箱
            if (from.contains("dzrhotc")) {
                // 企业邮箱必须使用SSL认证---start
                // 开启安全协议
                MailSSLSocketFactory mailSSLSocketFactory = null;
                try {
                    mailSSLSocketFactory = new MailSSLSocketFactory();
                    mailSSLSocketFactory.setTrustAllHosts(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                properties.put("mail.smtp.enable", "true");
                properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.put("mail.smtp.socketFactory.fallback", "false");
                properties.put("mail.smtp.socketFactory.port", port);
                // 企业邮箱必须使用SSL认证---end
            }
            Session session = Session.getInstance(properties, new Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });
            // 收件人
            List<String> receiveUserList = dto.getReceiveUserList();
            if (receiveUserList.isEmpty()) {
                BussinessException.E_100103.assertTrue(false);
            }
            String content = replaceSpecialchar(dto.getContent());
            String appendContent = replaceSpecialchar(dto.getAppendContent());
            InternetAddress[] to = new InternetAddress[receiveUserList.size()];
            for (int index=0;index<receiveUserList.size();index++) {
                to[index] = new InternetAddress(receiveUserList.get(index));
            }
            MimeMessage message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(dto.getTitle());
            helper.setText(content+appendContent,true);
            // 结算报告附件为空
            if (dto.getSettlementReportTempFileByte() == null|| dto.getSettlementReportTempFileByte().length==0){
                BussinessException.E_100105.assertTrue(false);
            }
            InputStreamSource iss = new ByteArrayResource(dto.getSettlementReportTempFileByte());
            //加载系统生成的文件到邮件中去
            helper.addAttachment(dto.getTempFileName(), iss);
            log.info("邮件发送开始");
            // 发送邮件
            Transport.send(message);
            log.info("邮件发送结束");
            // 获取邮件ID
            String messageId =  message.getMessageID();
            log.info("邮件ID="+messageId+"----邮件="+messageId);
            // 重发不添加发送记录, 只更新邮件ID和发送次数以及发送时间
            if (!dto.getReSend()){
                mailSendRecordService.add(dto,receiveUserList);
            }
           /* else {
                mailSendRecordService.updateReSendMessageId(dto.getReSendMailId(),messageId);
            }*/
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("邮件发送异常="+e.getMessage());
            /*if (CollectionUtils.isEmpty(dto.getReceiveUserList())){
                mailSendRecordService.addSendFailRecord(dto,dto.getSendStatus(),e.getMessage(),null,null);
            } else {
                for (String mail: dto.getReceiveUserList()){
                    mailSendRecordService.add(dto,dto.getSendStatus(),e.getMessage(),null,mail);
                }
            }*/
            return false;
        }
    }
    /**
     * 把邮件模板中特殊字符替换成对应的html
     * @param str 邮件内容
     * @return 返回替换后的哦内容
     */
    public String replaceSpecialchar(String str){
        if(str == null){
            return "";
        }
        return str.replaceAll("\\\\n","</br>").replaceAll("\n","</br>");
    }
}
