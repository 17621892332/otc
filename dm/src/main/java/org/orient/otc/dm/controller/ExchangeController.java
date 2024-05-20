package org.orient.otc.dm.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.dm.dto.ExchangeDeleteDto;
import org.orient.otc.dm.dto.ExchangeIdDto;
import org.orient.otc.dm.dto.ExchangePageDto;
import org.orient.otc.dm.dto.ExchangeSaveDto;
import org.orient.otc.dm.entity.Exchange;
import org.orient.otc.dm.service.ExchangeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Exchange)表控制层
 * @author makejava
 * @since 2023-07-20 13:44:57
 */
@Api(tags = "交易所")
@RestController
@RequestMapping("exchange")
public class ExchangeController  {
    /**
     * 服务对象
     */
    @Resource
    private ExchangeService exchangeService;

    /**
     * 查询所有数据
     * @return 所有数据
     */
    @GetMapping("list")
    @ApiOperation("交易所列表")
    public HttpResourceResponse<List<Exchange>> selectAll() {
        return HttpResourceResponse.success(this.exchangeService.list(new LambdaQueryWrapper<Exchange>().eq(Exchange::getIsDeleted,0)));
    }

    /**
     * 通过主键查询单条数据
     * @param dto 主键
     * @return 单条数据
     */
    @ApiOperation("交易所详情")
    @PostMapping("getById")
    public  HttpResourceResponse<Exchange> selectOne(@RequestBody ExchangeIdDto dto) {
        return HttpResourceResponse.success(this.exchangeService.getById(dto.getId()));
    }

    @ApiOperation("交易所分页查询")
    @PostMapping("/selectListByPage")
    public  HttpResourceResponse<IPage<Exchange>> selectListByPage(@RequestBody ExchangePageDto dto) {
        return HttpResourceResponse.success(this.exchangeService.selectListByPage(dto));
    }

    @ApiOperation(value="保存交易所信息")
    @PostMapping("/saveExchange")
    public  HttpResourceResponse<String> saveExchange(@RequestBody ExchangeSaveDto dto) {
        return HttpResourceResponse.success(this.exchangeService.saveExchange(dto));
    }

    @ApiOperation(value="删除交易所信息")
    @PostMapping("/deleteExchange")
    public  HttpResourceResponse<String> deleteExchange(@RequestBody ExchangeDeleteDto dto) {
        return HttpResourceResponse.success(this.exchangeService.deleteExchange(dto));
    }


}

