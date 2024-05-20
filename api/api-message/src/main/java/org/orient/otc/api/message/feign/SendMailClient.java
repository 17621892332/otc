package org.orient.otc.api.message.feign;

import org.orient.otc.api.message.config.EncodeConfiguration;
import org.orient.otc.api.message.dto.SendMailDto;
import org.orient.otc.api.message.vo.MailKeywordsConfigVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author dzrh
 */
@FeignClient(value = "messageserver",path = "/sendMail", contextId ="sendMail", configuration = EncodeConfiguration.class)
public interface SendMailClient {
    /**
     * 发送邮件
     * @param dto 入参
     */
    @PostMapping(value=FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/sendMail",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void sendMail(SendMailDto dto);

    /**
     * 邮件发送记录
     * @param dto
     */
    @PostMapping(value=FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/addSendMailRecord",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void addSendMailRecord(SendMailDto dto);

    /**
     * 获取邮件通配符关键字列表
     * @return 返回通配符list
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getMailKeywordsConfigLsit")
    List<MailKeywordsConfigVO> getMailKeywordsConfigLsit();

}
