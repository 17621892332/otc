package org.orient.otc.quote.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.jni.QuoteRequestDto;
import org.orient.otc.quote.service.JniSerevice;
import org.orient.otc.quote.vo.jni.QuoteResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jni")
@Api(tags = "so测试", description = "so测试")
public class JniController {
    @Autowired
    private JniSerevice jniSerevice;
    @PostMapping("/test")
    @ApiOperation("so测试")
    public HttpResourceResponse<QuoteResultVo> callSo(@RequestBody QuoteRequestDto quoteSoDto){
        return HttpResourceResponse.success(jniSerevice.callSo(quoteSoDto));
    }
}
