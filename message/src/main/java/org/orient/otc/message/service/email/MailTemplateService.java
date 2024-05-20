package org.orient.otc.message.service.email;

import org.orient.otc.message.dto.MailTemplateAddDto;
import org.orient.otc.message.dto.MailTemplateDeleteDto;
import org.orient.otc.message.dto.MailTemplateUpdateAsDefaultDto;
import org.orient.otc.message.dto.MailTemplateUpdateDto;
import org.orient.otc.message.entity.MailTemplate;

import java.util.List;

public interface MailTemplateService {

    MailTemplate getDefaultTemplate();

    List<MailTemplate> listAll();

    String addMailTemplate(MailTemplateAddDto dto);

    String updateTemplate(MailTemplateUpdateDto dto);

    String deleteMailTemplate(MailTemplateDeleteDto dto);

    String updateAsDefault(MailTemplateUpdateAsDefaultDto dto);
}
