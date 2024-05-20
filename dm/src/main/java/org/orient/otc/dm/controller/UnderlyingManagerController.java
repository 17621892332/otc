package org.orient.otc.dm.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.dm.vo.VarietyVo;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.exception.BussinessException;
import org.orient.otc.dm.dto.UnderlyingVolatilityDTO;
import org.orient.otc.dm.dto.UnderlyingVolatilityDelDto;
import org.orient.otc.dm.dto.underlying.*;
import org.orient.otc.dm.dto.variety.VarietyIdDto;
import org.orient.otc.dm.entity.UnderlyingManager;
import org.orient.otc.dm.service.UnderlyingManagerService;
import org.orient.otc.dm.service.VarietyService;
import org.orient.otc.dm.vo.UnderlyingVolatilityVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 标的合约
 */
@RestController
@RequestMapping("/underlying")
@Api(tags = "标的合约")
public class UnderlyingManagerController {
    @Resource
    private UnderlyingManagerService underlyingManagerService;
    @Resource
    private VarietyService varietyService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 获取所有合约列表
     * @param underlyingManagerQueryDto  合约状态
     * @return 合约列表
     */
    @ApiOperation("获取标的列表")
    @PostMapping("/list")
    public HttpResourceResponse<List<UnderlyingManager>> getList(@RequestBody @Valid UnderlyingManagerQueryDto underlyingManagerQueryDto){
        return HttpResourceResponse.success(underlyingManagerService.getList(underlyingManagerQueryDto));
    }

    /**
     * 通过标的代码获取合约信息
     * @param underlyingCode 合约代码
     * @return 合约信息
     */
    @ApiOperation("通过标的代码获取合约信息")
    @GetMapping("/getUnderlyingByCode")
    public HttpResourceResponse<UnderlyingManagerVO> getUnderlyingByCode(@RequestParam String underlyingCode) {
        String underlyingManagerStr = stringRedisTemplate.opsForValue().get(RedisAdapter.UNDERLYING_BY_CODE + underlyingCode);
        if (StringUtils.isNotBlank(underlyingManagerStr)) {
            return HttpResourceResponse.success(JSONObject.parseObject(underlyingManagerStr, UnderlyingManagerVO.class));
        } else {
            UnderlyingManagerVO underlyingManagerVO = underlyingManagerService.getUnderlyingVoByCode(underlyingCode);
            BussinessException.E_200001.assertTrue(Objects.nonNull(underlyingManagerVO), underlyingCode);
            if(underlyingManagerVO.getUpDownLimit()==null){
                VarietyVo varietyVo= varietyService.getVarietyById(underlyingManagerVO.getVarietyId());
                underlyingManagerVO.setUpDownLimit(varietyVo.getUpDownLimit());
            }
            stringRedisTemplate.opsForValue().set(RedisAdapter.UNDERLYING_BY_CODE + underlyingCode, JSONObject.toJSONString(underlyingManagerVO), 1, TimeUnit.HOURS);
            return HttpResourceResponse.success(underlyingManagerVO);
        }
    }

    @ApiOperation("设置主力合约附合约分红率")
    @PostMapping("setUnderlyingVolatility")
    public HttpResourceResponse<Boolean> setUnderlyingVolatility(@RequestBody @Valid List<UnderlyingVolatilityDTO> underlyingVolatilityDTOList){
        return HttpResourceResponse.success(underlyingManagerService.setUnderlyingVolatility(underlyingVolatilityDTOList));
    }
    @ApiOperation("删除主力合约附合约分红率")
    @PostMapping("delUnderlyingVolatility")
    public HttpResourceResponse<Boolean> delUnderlyingVolatility(@RequestBody @Valid UnderlyingVolatilityDelDto delDto){
        return HttpResourceResponse.success(underlyingManagerService.delUnderlyingVolatility(delDto));
    }
    @ApiOperation("获取主力合约附合约分红率")
    @PostMapping("getUnderlyingVolatility")
    public HttpResourceResponse<List<UnderlyingVolatilityVO>> getUnderlyingVolatility(@RequestBody @Valid VarietyIdDto varietyIdDto){
        return HttpResourceResponse.success(underlyingManagerService.getUnderlyingVolatility(varietyIdDto.getVarietyId()));
    }

    @ApiOperation("分页获取合约列表")
    @PostMapping("/page")
    public HttpResourceResponse<Page<UnderlyingManagerVO>> page(@RequestBody UnderlyingManagerPageQueryDto queryDto){
        return HttpResourceResponse.success(underlyingManagerService.queryUnderlyingList(queryDto));
    }

    @ApiOperation("新增合约")
    @PostMapping("/add")
    public HttpResourceResponse<String> add(@RequestBody @Valid  UnderlyingManagerAddDto addDto){
        return HttpResourceResponse.success(underlyingManagerService.addUnderlying(addDto));
    }

    @ApiOperation("修改合约")
    @PostMapping("/edit")
    public HttpResourceResponse<String> edit(@RequestBody @Valid UnderlyingManagerEditDto editDto){
        return HttpResourceResponse.success(underlyingManagerService.editUnderlying(editDto));
    }

    @ApiOperation("禁用或者取消禁用")
    @PostMapping("/enable")
    public HttpResourceResponse<String> enable(@RequestBody UnderlyingManagerEnableDto enableDto){

        return HttpResourceResponse.success(underlyingManagerService.updateUnderlyingEnable(enableDto));
    }

    @ApiOperation("获取处于禁止交易的合约名单 ")
    @PostMapping("/disableList")
    public HttpResourceResponse<Set<String>> disableList(){
        return HttpResourceResponse.success(underlyingManagerService.disableList());
    }

}
