package org.orient.message.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.api.message.dto.SendMailDto;
import org.orient.otc.message.MessageApplication;
import org.orient.otc.message.entity.MailSendRecord;
import org.orient.otc.message.entity.MailTemplate;
import org.orient.otc.message.mapper.MailSendRecordMapper;
import org.orient.otc.message.service.email.EmailServer;
import org.orient.otc.message.service.email.MailTemplateService;
import org.orient.otc.message.service.email.impl.RejectMailServerImpl;
import org.orient.otc.message.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessageApplication.class)
public class ServiceImplTest extends TestCase {
    @Resource
    MailTemplateService mailTemplateService;
    @Autowired
    MailSendRecordMapper mailSendRecordMapper;
    @Autowired
    EmailServer emailServer;
    @Autowired
    RejectMailServerImpl rejectMailServer;

    @Test
    public void getAllMailTemplateTest(){
        MailTemplate mailTemplate =  mailTemplateService.getDefaultTemplate();
        System.out.println("----"+ JSON.toJSONString(mailTemplate));
    }
    @Test
    public void getMailSendRecord(){
        // 如果一个小时之内没有退信, 视为成功, 更新数据库中状态为成功
        LambdaQueryWrapper<MailSendRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MailSendRecord::getIsDeleted,0);
        lambdaQueryWrapper.ge(MailSendRecord::getCreateTime, DateUtil.getPreHour());
        List<MailSendRecord> list = mailSendRecordMapper.selectList(lambdaQueryWrapper);
        System.out.println("--------"+list.size());
    }

    /**
     * 使用东证邮箱做测试发送
     */
    @Test
    public void sendMailTest(){
        SendMailDto dto = new SendMailDto();
        String suffix = "-06";
        dto.setTitle("测试邮件"+suffix);
        dto.setContent("测试邮件正文"+suffix);
        dto.setAppendContent("测试追加正文"+suffix);
        dto.setReceiveUserList(Arrays.asList("14qweqweqw1232ewqewq@qq.com"));
        dto.setReSend(false);
        List<String> receiveUserList = new ArrayList<>();
        receiveUserList.add("chengqiang@datadriver.com.cn");
        receiveUserList.add("1312qwwewdqdqdq@qq.com");
        dto.setReceiveUserList(receiveUserList);
        File file = new File("C://Users//dzrh//Desktop//webapi//start.sh");
        //dto.setSettlementReportFile(FileUtil.file2MultipartFile(file));
        dto.setTempFileName("临时文件名start.sh");
        emailServer.sendEMail(dto);
    }
    /**
     * 测试退信读取
     */
    @Test
    public void rejectMailTest() throws Exception {
        rejectMailServer.asyncDoRejectMail();
    }
}
