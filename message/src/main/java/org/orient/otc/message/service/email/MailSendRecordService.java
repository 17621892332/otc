package org.orient.otc.message.service.email;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.api.message.dto.SendMailDto;
import org.orient.otc.message.dto.DoSendMailDto;
import org.orient.otc.message.dto.MailSendRecordPageDto;
import org.orient.otc.message.dto.MultiSendMailDto;
import org.orient.otc.message.dto.ReSendMailDto;
import org.orient.otc.message.vo.MailSendRecordVO;

import java.util.List;

/**
 * @author chengqiang
 */
public interface MailSendRecordService {
    String multiSendMail(MultiSendMailDto dto);

    void add(SendMailDto dto,List<String> receiveUserList);

    void addSendFailRecord(SendMailDto sendMailDto,List<String> errorMailList, String failDesc,boolean allFail);

    IPage<MailSendRecordVO> selectListByPage(MailSendRecordPageDto dto);

    String reSend(ReSendMailDto dto);

    void updateReSendMessageId(Integer id,String messageId);

    void updateReSendCount(Integer id, int reSendCount, String mailType);

    void updateReSendStatusAndCount(Integer id, int sendStatus, int reSendCount,String mailType);

    void doSendMail(DoSendMailDto dto);

}
