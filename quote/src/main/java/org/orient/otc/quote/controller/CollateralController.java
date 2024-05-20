package org.orient.otc.quote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.collateral.*;
import org.orient.otc.quote.service.CollateralService;
import org.orient.otc.quote.vo.collateral.CollateralVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/trade/collateral")
@Api(tags = "抵押品", description = "抵押品")
@Slf4j
public class CollateralController {
    @Autowired
    CollateralService collateralService;

    @PostMapping("/selectListByPage")
    @ApiOperation("抵押品分页查询")
    public HttpResourceResponse<IPage<CollateralVO>> selectListByPage(@RequestBody CollateralPageListDto dto){
        return HttpResourceResponse.success(collateralService.selectListByPage(dto));
    }

    @PostMapping("/add")
    @ApiOperation("抵押品新增")
    public HttpResourceResponse<String> add(@RequestBody  @Valid CollateralAddDto dto){
        return HttpResourceResponse.success(collateralService.add(dto));
    }

    @PostMapping("/check")
    @ApiOperation("审核")
    public HttpResourceResponse<String> check(@RequestBody @Valid CollateralCheckDto dto){
        return collateralService.check(dto);
    }
    @PostMapping("/redemption")
    @ApiOperation("赎回")
    public HttpResourceResponse<String> redemption(@RequestBody @Valid CollateralRedemptionDto dto){
        return collateralService.redemption(dto);
    }

    @PostMapping("/update")
    @ApiOperation("修改抵押品信息")
    public HttpResourceResponse<String> update(@RequestBody @Valid CollateralUpdateDto dto){
        return HttpResourceResponse.success(collateralService.update(dto));
    }

    @PostMapping("/updateMarketPrice")
    @ApiOperation("修改盯市价格")
    public HttpResourceResponse<String> updateMarketPrice(@RequestBody @Valid CollateralUpdateMarketPriceDto dto){
        return HttpResourceResponse.success(collateralService.updateMarketPrice(dto));
    }

    /**
     * 获取盯市价格
     * @param dto 抵押品名称
     * @return  盯市价格
     */
    @PostMapping("/getMarketPrice")
    @ApiOperation("获取盯市价格")
    public HttpResourceResponse<BigDecimal> getMarketPrice(@RequestBody @Valid CollateralGetMarketPriceDto dto){
        return HttpResourceResponse.success(collateralService.getMarketPrice(dto));
    }





}
