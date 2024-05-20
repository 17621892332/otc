package org.orient.otc.message.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.message.dto.MailTemplateAddDto;
import org.orient.otc.message.dto.MailTemplateDeleteDto;
import org.orient.otc.message.dto.MailTemplateUpdateAsDefaultDto;
import org.orient.otc.message.dto.MailTemplateUpdateDto;
import org.orient.otc.message.entity.MailTemplate;
import org.orient.otc.message.service.email.MailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mailTemplate")
@Api(tags = "邮件模板")
public class MailTemplateController {
    @Autowired
    MailTemplateService mailTemplateService;

    @ApiOperation("新增邮件模板")
    @RequestMapping("/addTemplate")
    public HttpResourceResponse<String> addMailTemplate(@RequestBody @Valid MailTemplateAddDto dto){
        return HttpResourceResponse.success(mailTemplateService.addMailTemplate(dto));
    }

    @ApiOperation("修改邮件模板")
    @RequestMapping("/updateTemplate")
    public HttpResourceResponse<String> updateTemplate(@RequestBody @Valid MailTemplateUpdateDto dto){
        return HttpResourceResponse.success(mailTemplateService.updateTemplate(dto));
    }

    @ApiOperation("邮件模板设为默认")
    @RequestMapping("/updateAsDefault")
    public HttpResourceResponse<String> updateAsDefault(@RequestBody @Valid MailTemplateUpdateAsDefaultDto dto){
        return HttpResourceResponse.success(mailTemplateService.updateAsDefault(dto));
    }

    @ApiOperation("获取默认模板")
    @RequestMapping("/getDefaultTemplate")
    public HttpResourceResponse<MailTemplate> getDefaultTemplate(){
        return HttpResourceResponse.success(mailTemplateService.getDefaultTemplate());
    }


    @ApiOperation("获取所有的邮件模板")
    @RequestMapping("/getAllMailTemplate")
    public HttpResourceResponse<List<MailTemplate>> getAllMailTemplate(){
        return HttpResourceResponse.success(mailTemplateService.listAll());
    }

    @ApiOperation("删除邮件模板")
    @RequestMapping("/deleteMailTemplate")
    public HttpResourceResponse<String> deleteMailTemplate(@RequestBody @Valid MailTemplateDeleteDto dto){
        return HttpResourceResponse.success(mailTemplateService.deleteMailTemplate(dto));
    }
}
