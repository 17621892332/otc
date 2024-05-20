package org.orient.otc.message.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.message.dto.MailSendRecordPageDto;
import org.orient.otc.message.dto.MultiSendMailDto;
import org.orient.otc.message.service.email.EmailServer;
import org.orient.otc.message.service.email.MailSendRecordService;
import org.orient.otc.message.vo.MailSendRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mailSendRecord")
public class MailSendRecordController {
    @Autowired
    MailSendRecordService mailSendRecordService;

    /**
     * 邮件发送记录分页查询
     * @param dto 入参
     * @return 返回值
     */
    @RequestMapping("/selectListByPage")
    public HttpResourceResponse<IPage<MailSendRecordVO>> selectListByPage(@RequestBody MailSendRecordPageDto dto){
        return HttpResourceResponse.success( mailSendRecordService.selectListByPage(dto));
    }
}
