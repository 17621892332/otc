package org.orient.otc.message.service.email;

import org.orient.otc.api.message.dto.SendMailDto;

public interface EmailServer {
 /**
  * 发送邮件
  * @param dto 入参
  * @return 返回是否发送成功
  */
 boolean sendEMail(SendMailDto dto);

}
