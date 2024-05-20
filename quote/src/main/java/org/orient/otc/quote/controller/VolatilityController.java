package org.orient.otc.quote.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.quote.dto.VolatilityQueryDto;
import org.orient.otc.api.quote.dto.VolatityQueryCodeListDto;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.volatility.DeltaVolToStrikeVolDto;
import org.orient.otc.quote.dto.volatility.LinearInterpVolSurfaceDto;
import org.orient.otc.quote.dto.volatility.VolatilityListDto;
import org.orient.otc.quote.entity.Volatility;
import org.orient.otc.quote.service.VolatilityService;
import org.orient.otc.quote.vo.volatility.DeltaVolToStrikeVolVo;
import org.orient.otc.quote.vo.volatility.LinearInterpVolSurfaceVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 波动率
 */
@RestController
@RequestMapping("/volatility")
@Api(tags = "波动率")
public class VolatilityController {
    @Resource
    private VolatilityService volatilityService;

    /**
     * 插入更新波动率
     * @param volatilityList 波动率列表
     * @return 保存结果
     */
    @PostMapping("/insertOrUpdate")
    @ApiOperation("插入更新波动率")
    @CheckPermission("quote::volatility::insertOrUpdate")
    public HttpResourceResponse<String> insertOrUpdate(@RequestBody @Valid VolatilityListDto volatilityList){
        return HttpResourceResponse.success(volatilityService.saveVolatility(volatilityList));
    }

    /**
     * 根据标的和日期查询波动率
     * @param volatilityQueryDto 查询条件
     * @return 波动率列表
     */
    @PostMapping("/queryByUnderlyingId")
    @ApiOperation("根据标的和日期查询波动率")
    @CheckPermission("quote::volatility::queryByUnderlyingId")
    public HttpResourceResponse<List<Volatility>> getVolatility(@RequestBody @Valid VolatilityQueryDto volatilityQueryDto){
        return HttpResourceResponse.success(volatilityService.getVolatility(volatilityQueryDto));
    }

    /**
     * 计算波动率插值-批量
     * @param linearInterpVolSurfaceDto 插值参数
     * @return 插值结果
     */
    @PostMapping("/linearInterpVolSurface")
    @ApiOperation("计算波动率插值")
    @CheckPermission("quote::volatility::linearInterpVolSurface")
    public HttpResourceResponse<LinearInterpVolSurfaceVo> linearInterpVolSurface(@RequestBody @Valid LinearInterpVolSurfaceDto linearInterpVolSurfaceDto){
        return HttpResourceResponse.success(volatilityService.linearInterpVolSurface(linearInterpVolSurfaceDto));
    }

    /**
     * 计算波动率插值-批量
     * @param list 插值列表
     * @return 插值结果
     */
    @PostMapping("/linearInterpVolSurfaceBatch")
    @ApiOperation("计算波动率插值-批量")
    @CheckPermission("quote::volatility::linearInterpVolSurfaceBatch")
    public HttpResourceResponse<List<LinearInterpVolSurfaceVo>> linearInterpVolSurfaceBatch(@RequestBody @Valid List<LinearInterpVolSurfaceDto> list){
        return HttpResourceResponse.success(volatilityService.linearInterpVolSurfaceBatch(list));
    }

    /**
     * delta波动率转strike波动率
     * @param data 转换前波动率
     * @return 转换后波动率
     */
    @PostMapping("/deltaVolToStrikeVol")
    @ApiOperation("delta波动率转strike波动率")
    @CheckPermission("quote::volatility::deltaVolToStrikeVol")
    public HttpResourceResponse<DeltaVolToStrikeVolVo> deltaVolToStrikeVol(@RequestBody @Valid DeltaVolToStrikeVolDto data){
        return HttpResourceResponse.success(volatilityService.deltaVolToStrikeVol(data));
    }

    /**
     * 获取有波动率的合约列表
     * @param dto 请求对象
     * @return 合约列表
     */
    @ApiModelProperty(value = "获取有波动率的合约列表")
    @PostMapping("/getUnderlyingCodeListByVol")
    @CheckPermission("quote::volatility::getUnderlyingCodeListByVol")
    public HttpResourceResponse<List<UnderlyingManagerVO>> getUnderlyingCodeListByVol(@RequestBody VolatityQueryCodeListDto dto){
        return HttpResourceResponse.success(volatilityService.getUnderlyingCodeListByVol(dto));
    }

}
