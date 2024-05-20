package org.orient.otc.dm.feign;

import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.dm.enums.UnderlyingState;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.dm.vo.VarietyVo;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.security.exception.BussinessException;
import org.orient.otc.dm.entity.UnderlyingManager;
import org.orient.otc.dm.entity.Variety;
import org.orient.otc.dm.service.UnderlyingManagerService;
import org.orient.otc.dm.service.VarietyService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 合约内部接口
 */
@RestController
@RequestMapping("/underlying")
public class UnderlyingFeignController implements UnderlyingManagerClient {
    @Resource
    private UnderlyingManagerService underlyingManagerService;

    @Resource
    private VarietyService varietyService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UnderlyingManagerVO getUnderlyingByCode(String code) {
        String underlyingManagerStr =  stringRedisTemplate.opsForValue().get(RedisAdapter.UNDERLYING_BY_CODE+code);
        if (StringUtils.isNotBlank(underlyingManagerStr)){
            return JSONObject.parseObject(underlyingManagerStr, UnderlyingManagerVO.class);
        }else {
            UnderlyingManagerVO vo=  underlyingManagerService.getUnderlyingVoByCode(code);
            BussinessException.E_200001.assertTrue(Objects.nonNull(vo),code);
            if(vo.getUpDownLimit()==null){
            VarietyVo varietyVo= varietyService.getVarietyById(vo.getVarietyId());
                vo.setUpDownLimit(varietyVo.getUpDownLimit());
                vo.setUnderlyingAssetType(vo.getUnderlyingAssetType());
                vo.setUnit(vo.getUnit());
                vo.setVarietyName(varietyVo.getVarietyName());
            }
            stringRedisTemplate.opsForValue().set(RedisAdapter.UNDERLYING_BY_CODE+code,JSONObject.toJSONString(vo),1,TimeUnit.HOURS);
            return vo;
        }
    }

    @Override
    public List<UnderlyingManagerVO> getUnderlyingByCodes(Set<String> codes) {
        List<UnderlyingManagerVO> returnList = new ArrayList<>();
        // redis中没有的code
        Set<String> redisNoCode = new HashSet<>();
        for(String code : codes) {
            String underlyingManagerStr =  stringRedisTemplate.opsForValue().get(RedisAdapter.UNDERLYING_BY_CODE+code);
            if (StringUtils.isNotBlank(underlyingManagerStr)){
                returnList.add(JSONObject.parseObject(underlyingManagerStr, UnderlyingManagerVO.class));
            } else {
                redisNoCode.add(code);
            }
        }
        // redis中没有的code要添加进去
        if (!redisNoCode.isEmpty()) {
            List<UnderlyingManagerVO> underlyingManagerList=  underlyingManagerService.getUnderlyingVoByCodes(redisNoCode);
            if (null != underlyingManagerList && !underlyingManagerList.isEmpty()) {
                returnList.addAll(underlyingManagerList);
                for (UnderlyingManagerVO vo : underlyingManagerList) {
                    if(vo.getUpDownLimit()==null){
                        VarietyVo varietyVo= varietyService.getVarietyById(vo.getVarietyId());
                        vo.setUpDownLimit(varietyVo.getUpDownLimit());
                        vo.setUnderlyingAssetType(vo.getUnderlyingAssetType());
                        vo.setUnit(vo.getUnit());
                        vo.setVarietyName(varietyVo.getVarietyName());
                    }
                    stringRedisTemplate.opsForValue().set(RedisAdapter.UNDERLYING_BY_CODE+vo.getUnderlyingCode(),JSONObject.toJSONString(vo),1,TimeUnit.HOURS);
                }
            }
        }
        return returnList;
    }


    @Override
    public List<UnderlyingManagerVO> getUnderlyingList() {
        LambdaQueryWrapper<UnderlyingManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnderlyingManager::getUnderlyingState, UnderlyingState.Live);
        queryWrapper.eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
        return underlyingManagerService.listVo(queryWrapper, UnderlyingManagerVO.class);
    }

    @Override
    public List<UnderlyingManagerVO> getUnderlyingListByVarietyId(Integer varietyId) {
        LambdaQueryWrapper<UnderlyingManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnderlyingManager::getUnderlyingState, UnderlyingState.Live);
        queryWrapper.eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.eq(UnderlyingManager::getVarietyId,varietyId);
        return underlyingManagerService.listVo(queryWrapper, UnderlyingManagerVO.class);
    }

    @Override
    public List<UnderlyingManagerVO> getUnderlyingListByVarietyIds(List<Integer> varietyIds) {
        LambdaQueryWrapper<UnderlyingManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.in(UnderlyingManager::getVarietyId,varietyIds);
        return underlyingManagerService.listVo(queryWrapper, UnderlyingManagerVO.class);
    }

    /**
     * 批量保存或更新合约信息
     * @param dtoList 合约列表
     * @return true 更新成功  false 更新失败
     */
    @Override
    public Boolean saveBatch(List<UnderlyingManagerVO> dtoList) {
        List<UnderlyingManager> list = CglibUtil.copyList(dtoList,UnderlyingManager::new);
        return underlyingManagerService.saveOrUpdateBatchByCode(list);
    }

    @Override
    public SettlementVO updateUnderlyingState() {
        return underlyingManagerService.updateUnderlyingState();
    }


    @Override
    public List<UnderlyingManagerVO> getMainUnderlyingList() {
        return underlyingManagerService.getMainUnderlyingList();
    }

    @Override
    public List<UnderlyingManagerVO> getUnderlyingListByAssetType(AssetTypeEnum assetType, LocalDate queryDate) {
       List<Variety> varietyList= varietyService.getVarietListByAssetType(assetType);
       List<Integer> varietIdList = varietyList.stream().map(Variety::getId).collect(Collectors.toList());
        return underlyingManagerService.getUnderlyingByVarietyList(varietIdList,queryDate);
    }
}
