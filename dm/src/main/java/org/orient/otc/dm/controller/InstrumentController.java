package org.orient.otc.dm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.NoCheckLogin;
import org.orient.otc.dm.dto.InstrumentDetailDto;
import org.orient.otc.dm.dto.InstrumentPageDto;
import org.orient.otc.dm.dto.InstrumentUpdateDto;
import org.orient.otc.dm.dto.QueryInstrumentPage;
import org.orient.otc.dm.entity.Instrument;
import org.orient.otc.dm.service.InstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/instrument")
@Api(tags = "场内合约", description = "合约接口")
public class InstrumentController {
    @Autowired
    private InstrumentService instrumentService;
    @ApiOperation("分页获合约的列表")
    @PostMapping("/getListByPage")
    @NoCheckLogin
    public HttpResourceResponse<Page<Instrument>> getListByPage(@RequestBody @Valid QueryInstrumentPage queryInstrumentPage){
        return HttpResourceResponse.success(instrumentService.getListByPage(queryInstrumentPage));
    }

    @ApiOperation("修改合约-VUE端使用")
    @PostMapping("/update")
    public HttpResourceResponse<String> updateInstrument(@RequestBody @Valid InstrumentUpdateDto dto) throws Exception {
        return HttpResourceResponse.success(instrumentService.updateInstrument(dto));
    }
    @ApiOperation("获取合约详情-VUE端使用")
    @PostMapping("/getInstrumentDetail")
    public HttpResourceResponse<Instrument> getInstrumentDetail(@RequestBody @Valid InstrumentDetailDto dto){
        return HttpResourceResponse.success(instrumentService.getById(dto.getInstrumentId()));
    }

    @ApiOperation("分页获合约的列表-VUE端使用")
    @PostMapping("/selectListByPage")
    @NoCheckLogin
    public HttpResourceResponse<Page<Instrument>> selectListByPage(@RequestBody InstrumentPageDto dto){
        return HttpResourceResponse.success(instrumentService.selectListByPage(dto));
    }

}
