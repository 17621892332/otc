package org.orient.otc.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.system.dto.SqlRequestDto;
import org.orient.otc.system.service.SqlConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/sql")
@Api(tags = "sql 配置", description = "sql 配置")
public class SqlConfigController {
    @Autowired
    private SqlConfigService sqlConfigService;
    @PostMapping("/getSqlResult")
    @ApiOperation("调用执行sql")
    @CheckPermission("system::sql::getSqlResult")
    public HttpResourceResponse<Object> getSqlResult(@RequestBody @Valid SqlRequestDto sqlRequest){
        return HttpResourceResponse.success(sqlConfigService.getSqlResult(sqlRequest));
    }
}
