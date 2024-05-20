package org.orient.otc.quote.controller.settlement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.SnowKnockedinLogPageDto;
import org.orient.otc.quote.entity.SnowKnockedinLog;
import org.orient.otc.quote.service.SnowKnockedinLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/snowKnockedinLog")
@Api(tags = "雪球敲入记录", description = "雪球敲入记录")
@Slf4j
public class SnowKnockedinLogController {
    @Autowired
    SnowKnockedinLogService snowKnockedinLogService;

    @PostMapping("/selectListByPage")
    @ApiOperation("分页查询")
    public HttpResourceResponse<IPage<SnowKnockedinLog>> selectListByPage(@RequestBody SnowKnockedinLogPageDto dto){
        return HttpResourceResponse.success(snowKnockedinLogService.selectListByPage(dto));
    }


}
