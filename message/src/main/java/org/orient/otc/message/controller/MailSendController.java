package org.orient.otc.message.controller;

import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.message.dto.MultiSendMailDto;
import org.orient.otc.message.dto.ReSendMailDto;
import org.orient.otc.message.service.email.EmailServer;
import org.orient.otc.message.service.email.MailSendRecordService;
import org.orient.otc.message.service.email.RejectMailServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mailSend")
public class MailSendController {
    @Autowired
    EmailServer emailServer;
    @Autowired
    MailSendRecordService mailSendRecordService;
    @Autowired
    RejectMailServer rejectMailServer;

    /**
     * 批量发送邮件
     * @param dto 入参
     * @return 返回提示信息
     */
    @PostMapping("/multiSendMail")
    public HttpResourceResponse<String> multiSendMail(@RequestBody MultiSendMailDto dto){
        return HttpResourceResponse.success( mailSendRecordService.multiSendMail(dto));
    }

    /**
     * 邮件重发
     * @param dto 入参
     * @return 返回操作信息
     */
    @PostMapping("/reSend")
    public HttpResourceResponse<String> reSend(@RequestBody ReSendMailDto dto){
        return HttpResourceResponse.success( mailSendRecordService.reSend(dto));
    }

    /**
     * 处理退件信息
     * @return 返回操作提示
     * @throws Exception 异常
     */
    @PostMapping("/doRejectMail")
    public HttpResourceResponse<String> doRejectMail() throws Exception {
        rejectMailServer.asyncDoRejectMail();
        return HttpResourceResponse.success("操作成功");
    }


}
