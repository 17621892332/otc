package org.orient.otc.message.feign;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.message.dto.ReSendDto;
import org.orient.otc.api.message.dto.SendMailDto;
import org.orient.otc.api.message.feign.SendMailClient;
import org.orient.otc.api.message.vo.MailKeywordsConfigVO;
import org.orient.otc.message.dto.DoSendMailDto;
import org.orient.otc.message.entity.MailKeywordsConfig;
import org.orient.otc.message.entity.MailTemplate;
import org.orient.otc.message.mapper.MailTemplateMapper;
import org.orient.otc.message.service.email.EmailServer;
import org.orient.otc.message.service.email.MailKeywordsConfigService;
import org.orient.otc.message.service.email.MailSendRecordService;
import org.orient.otc.message.service.email.RejectMailServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sendMail")
@Slf4j
public class SendMailFeignController implements SendMailClient {
    @Autowired
    EmailServer emailServer;
    @Autowired
    MailKeywordsConfigService mailKeywordsConfigService;
    @Autowired
    MailSendRecordService mailSendRecordService;
    @Autowired
    MailTemplateMapper mailTemplateMapper;
    @Autowired
    RejectMailServer rejectMailServer;

    @Override
    public void sendMail(SendMailDto dto) {
        log.info("结算报告邮件发送入参="+JSON.toJSONString(dto));
        //emailServer.sendEMail(dto);
        // 1. 获取模板
        MailTemplate mailTemplate = mailTemplateMapper.selectById(dto.getMailTemplateId());
        ReSendDto reSendDto = JSON.parseObject(dto.getReSendParams(),ReSendDto.class);
        //ThreadContext.setAuthorizeInfo(JSON.parseObject(dto.getAuthorizeInfo(), AuthorizeInfo.class));
        DoSendMailDto doSendMailDto = new DoSendMailDto();
        doSendMailDto.setMailTemplate(mailTemplate);
        doSendMailDto.setClientId(dto.getClientId());
        doSendMailDto.setQueryDate(reSendDto.getEndDate());
        doSendMailDto.setSendType(0);
        doSendMailDto.setMailReSendRecord(null);
        doSendMailDto.setStartDate(reSendDto.getStartDate());
        doSendMailDto.setIsAppendMail(dto.getIsAppendMail());
        doSendMailDto.setReportTypeSet(reSendDto.getReportTypeSet());
        doSendMailDto.setReceiveUserList(dto.getReceiveUserList());
        doSendMailDto.setAuthorizeInfo(dto.getAuthorizeInfo());
        mailSendRecordService.doSendMail(doSendMailDto);
        //mailSendRecordService.doSendMail(mailTemplate,dto.getClientId(),reSendDto.getEndDate(),0,null,reSendDto.getStartDate(),dto.getIsAppendMail(),reSendDto.getReportTypeSet(),dto.getReceiveUserList());
        rejectMailServer.doRejectMailList();
    }

    @Override
    public List<MailKeywordsConfigVO> getMailKeywordsConfigLsit() {
        List<MailKeywordsConfig> mailKeywordsConfigList = mailKeywordsConfigService.getAll();
        if(!mailKeywordsConfigList.isEmpty()){
            return mailKeywordsConfigList.stream().map(entity->{
                MailKeywordsConfigVO vo = new MailKeywordsConfigVO();
                BeanUtils.copyProperties(entity,vo);
                return vo;
            }).collect(Collectors.toList());
        } else{
            return null;
        }
    }

    @Override
    public void addSendMailRecord(SendMailDto dto) {
        /*if (CollectionUtils.isEmpty(dto.getReceiveUserList())){
            mailSendRecordService.add(dto,dto.getSendStatus(),dto.getSendFailDesc(),null,null);
        } else {
            for (String mail: dto.getReceiveUserList()){
                mailSendRecordService.add(dto,dto.getSendStatus(),dto.getSendFailDesc(),null,mail);
            }
        }*/
    }
}
