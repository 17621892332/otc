package org.orient.otc.message.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.message.dto.MailKeywordsConfigAddDto;
import org.orient.otc.message.dto.MailKeywordsConfigDeleteDto;
import org.orient.otc.message.dto.MailKeywordsConfigUpdateDto;
import org.orient.otc.message.entity.MailKeywordsConfig;
import org.orient.otc.message.service.email.MailKeywordsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mailKeywordsConfig")
@Api(tags = "邮件关键字配置")
public class MailKeywordsConfigController {
    @Autowired
    MailKeywordsConfigService mailKeywordsConfigService;

    @ApiOperation("添加通配符关键字配置")
    @RequestMapping("/add")
    public HttpResourceResponse<String> addConfig(@RequestBody @Valid MailKeywordsConfigAddDto dto){
        return HttpResourceResponse.success(mailKeywordsConfigService.add(dto));
    }
    @ApiOperation("获取所有通配符关键字配置")
    @RequestMapping("/getAll")
    public HttpResourceResponse<List<MailKeywordsConfig>> getAll(){
        return HttpResourceResponse.success(mailKeywordsConfigService.getAll());
    }

    @ApiOperation("修改通配符关键字配置")
    @RequestMapping("/update")
    public HttpResourceResponse<String> updateConfig(@RequestBody @Valid MailKeywordsConfigUpdateDto dto){
        return HttpResourceResponse.success(mailKeywordsConfigService.updateConfig(dto));
    }
    @ApiOperation("删除通配符")
    @RequestMapping("/delete")
    public HttpResourceResponse<String> deleteConfig(@RequestBody @Valid MailKeywordsConfigDeleteDto dto){
        return HttpResourceResponse.success(mailKeywordsConfigService.deleteConfig(dto));
    }


}
