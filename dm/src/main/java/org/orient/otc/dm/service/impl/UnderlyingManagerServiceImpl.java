package org.orient.otc.dm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.enums.MainContractEnum;
import org.orient.otc.api.dm.enums.UnderlyingState;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.quote.dto.UnderlyingVolatilityFeignDto;
import org.orient.otc.api.quote.dto.VolatilityQueryDto;
import org.orient.otc.api.quote.feign.VolatilityClient;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.exception.BaseException;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.dm.dto.UnderlyingVolatilityDTO;
import org.orient.otc.dm.dto.UnderlyingVolatilityDelDto;
import org.orient.otc.dm.dto.underlying.*;
import org.orient.otc.dm.entity.UnderlyingManager;
import org.orient.otc.dm.entity.Variety;
import org.orient.otc.dm.exception.BussinessException;
import org.orient.otc.dm.mapper.UnderlyingManagerMapper;
import org.orient.otc.dm.service.UnderlyingManagerService;
import org.orient.otc.dm.service.VarietyService;
import org.orient.otc.dm.vo.UnderlyingVolatilityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合约服务实现
 */
@Service
public class UnderlyingManagerServiceImpl extends ServiceImpl<BaseMapper<UnderlyingManager>, UnderlyingManager> implements UnderlyingManagerService {
    @Resource
    private UnderlyingManagerMapper underlyingManagerMapper;


    @Resource
    private VarietyService varietyService;

    @Resource
    private VolatilityClient volatilityClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SystemConfigUtil systemConfigUtil;

    @Override
    public List<UnderlyingManager> getList(UnderlyingManagerQueryDto underlyingManagerQueryDto) {

        LambdaQueryWrapper<UnderlyingManager> underlyingManagerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(underlyingManagerQueryDto.getVarietyId())) {
            underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getVarietyId, underlyingManagerQueryDto.getVarietyId());
        }
        if (Objects.nonNull(underlyingManagerQueryDto.getUnderlyingState())) {
            underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getUnderlyingState, underlyingManagerQueryDto.getUnderlyingState());
        }

        underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getIsDeleted, 0).orderByAsc(UnderlyingManager::getUnderlyingCode);
        return underlyingManagerMapper.selectList(underlyingManagerLambdaQueryWrapper);
    }


    @Override
    public UnderlyingManagerVO getUnderlyingVoByCode(String underlyingCode) {
        LambdaQueryWrapper<UnderlyingManager> underlyingManagerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getUnderlyingCode, underlyingCode);
        underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getIsDeleted, 0);
        UnderlyingManagerVO underlyingManagerVO = this.getVoOne(underlyingManagerLambdaQueryWrapper, UnderlyingManagerVO.class);
        Variety variety = varietyService.getById(underlyingManagerVO.getVarietyId());
        underlyingManagerVO.setVarietyCode(variety.getVarietyCode());
        underlyingManagerVO.setVarietyName(variety.getVarietyName());
        underlyingManagerVO.setUnderlyingAssetType(variety.getUnderlyingAssetType());
        underlyingManagerVO.setUnit(variety.getUnit());
        return underlyingManagerVO;
    }

    @Override
    public List<UnderlyingManagerVO> getUnderlyingVoByCodes(Set<String> codes) {
        LambdaQueryWrapper<UnderlyingManager> underlyingManagerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        underlyingManagerLambdaQueryWrapper.in(UnderlyingManager::getUnderlyingCode, codes);
        underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
        List<UnderlyingManager> list = underlyingManagerMapper.selectList(underlyingManagerLambdaQueryWrapper);
        // 获取所有品种ID
        Set<Integer> varietyIdSet = list.stream().map(UnderlyingManager::getVarietyId).collect(Collectors.toSet());
        // key = 品种ID , value = 品种obj
        Map<Integer,Variety> varietyMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(varietyIdSet)){
            LambdaQueryWrapper<Variety> varietyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            varietyLambdaQueryWrapper.in(Variety::getId,varietyIdSet);
            varietyLambdaQueryWrapper.eq(Variety::getIsDeleted,IsDeletedEnum.NO);
            List<Variety> varietyList = varietyService.list(varietyLambdaQueryWrapper);
            varietyMap = varietyList.stream().collect(Collectors.toMap(Variety::getId, item->item,(v1, v2)->v2));
        }
        Map<Integer, Variety> finalVarietyMap = varietyMap;
        return list.stream().map(item -> {
            UnderlyingManagerVO vo = new UnderlyingManagerVO();
            BeanUtils.copyProperties(item, vo);
            if (finalVarietyMap.containsKey(item.getVarietyId())) {
                Variety variety = finalVarietyMap.get(item.getVarietyId());
                vo.setVarietyCode(variety.getVarietyCode());
                vo.setVarietyName(variety.getVarietyName());
                vo.setUnderlyingAssetType(variety.getUnderlyingAssetType());
                vo.setUnit(variety.getUnit());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean saveOrUpdateBatchByCode(List<UnderlyingManager> list) {
        for (UnderlyingManager vo : list) {
            LambdaQueryWrapper<UnderlyingManager> underlyingManagerLambdaQueryWrapper = new LambdaQueryWrapper<>();
            underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getUnderlyingCode, vo.getUnderlyingCode());
            underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getIsDeleted, 0);
            UnderlyingManager underlyingManager = this.getBaseMapper().selectOne(underlyingManagerLambdaQueryWrapper);
            if (underlyingManager != null) {
                vo.setId(underlyingManager.getId());
                vo.setCreatorId(underlyingManager.getCreatorId());
                vo.setCreateTime(LocalDateTime.now());
            }
            this.saveOrUpdate(vo);
        }

        return Boolean.TRUE;
    }

    @Override
    public SettlementVO updateUnderlyingState() {
        SettlementVO settlementVo = new SettlementVO();

        UnderlyingManager underlyingManager = new UnderlyingManager();
        underlyingManager.setUnderlyingState(UnderlyingState.Matured);

        LambdaUpdateWrapper<UnderlyingManager> underlyingManagerLambdaQueryWrapper = new LambdaUpdateWrapper<>();
        underlyingManagerLambdaQueryWrapper.le(UnderlyingManager::getExpireDate, LocalDate.now());
        underlyingManagerLambdaQueryWrapper.ne(UnderlyingManager::getUnderlyingState, UnderlyingState.Matured);
        underlyingManagerLambdaQueryWrapper.eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
        int count = underlyingManagerMapper.update(underlyingManager, underlyingManagerLambdaQueryWrapper);
        settlementVo.setIsSuccess(Boolean.TRUE);
        if (settlementVo.getIsSuccess()) {
            settlementVo.setMsg("更新合约状态数量为：" + count);
        } else {
            settlementVo.setMsg("更新合约状态失败");
        }
        return settlementVo;
    }

    @Override
    @Transactional
    public Boolean setUnderlyingVolatility(List<UnderlyingVolatilityDTO> underlyingVolatilityDTOList) {
        //校验主力合约是否存在波动率
        UnderlyingVolatilityDTO mainUnderlying = underlyingVolatilityDTOList.stream()
                .filter(a -> a.getMainContract() == MainContractEnum.yes)
                .findFirst().orElseThrow(() -> new BaseException(BussinessException.E_600103, "主力合约不存在"));
        VolatilityQueryDto volatilityQueryDto = new VolatilityQueryDto();
        volatilityQueryDto.setUnderlyingCode(mainUnderlying.getUnderlyingCode());
        volatilityQueryDto.setQuotationDate(systemConfigUtil.getTradeDay());
        BussinessException.E_620002.assertTrue(volatilityClient.checkHaveVolatility(volatilityQueryDto));
        List<Integer> varietyIdList = underlyingVolatilityDTOList.stream().map(UnderlyingVolatilityDTO::getVarietyId).distinct().collect(Collectors.toList());
        BussinessException.E_600103.assertTrue(varietyIdList.size()==1,"BenchMark品种必须一致");
        //更新保存到数据
        for (UnderlyingVolatilityDTO underlyingVolatilityDto : underlyingVolatilityDTOList) {
            LambdaUpdateWrapper<UnderlyingManager> lambdaUpdateWrapper = new LambdaUpdateWrapper<UnderlyingManager>()
                    .eq(UnderlyingManager::getUnderlyingCode, underlyingVolatilityDto.getUnderlyingCode())
                    .eq(UnderlyingManager::getUnderlyingState, UnderlyingState.Live)
                    .eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
            UnderlyingManager underlyingManager = underlyingManagerMapper.selectOne(lambdaUpdateWrapper);
            if (underlyingManager == null) {
                BussinessException.E_620001.doThrow();
                return Boolean.FALSE;
            }
            underlyingManager.setMainContract(underlyingVolatilityDto.getMainContract());
            //仅有传入分红率不为空并且不等于系统分红率时才生效
            underlyingManager.setDividendYield(underlyingVolatilityDto.getDividendYield() != null &&
                    underlyingVolatilityDto.getDividendYield().compareTo(systemConfigUtil.getDividendYield()) != 0
                    ? underlyingVolatilityDto.getDividendYield() : null);
            lambdaUpdateWrapper.set(UnderlyingManager::getVolOffset, underlyingVolatilityDto.getVolOffset());
            underlyingManagerMapper.update(underlyingManager, lambdaUpdateWrapper);
            stringRedisTemplate.delete(RedisAdapter.UNDERLYING_BY_CODE + underlyingVolatilityDto.getUnderlyingCode());
        }
        //生成波动率
        List<UnderlyingVolatilityFeignDto> underlyingVolatilityFeignDtoList = CglibUtil.copyList(underlyingVolatilityDTOList, UnderlyingVolatilityFeignDto::new
                , (s, t) -> t.setMainContract(s.getMainContract() == MainContractEnum.yes ? Boolean.TRUE : Boolean.FALSE));
        volatilityClient.updateVolByOffset(underlyingVolatilityFeignDtoList);
        return Boolean.TRUE;
    }

    @Override
    public Boolean delUnderlyingVolatility(UnderlyingVolatilityDelDto delDto) {
        LambdaUpdateWrapper<UnderlyingManager> lambdaQueryWrapper = new LambdaUpdateWrapper<UnderlyingManager>()
                .eq(UnderlyingManager::getUnderlyingCode, delDto.getUnderlyingCode())
                .eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
        UnderlyingManager underlyingManager = underlyingManagerMapper.selectOne(lambdaQueryWrapper);
        lambdaQueryWrapper.set(UnderlyingManager::getMainContract, null);
        underlyingManagerMapper.update(underlyingManager, lambdaQueryWrapper);
        stringRedisTemplate.delete(RedisAdapter.UNDERLYING_BY_CODE + delDto.getUnderlyingCode());
        return Boolean.TRUE;
    }

    @Override
    public List<UnderlyingVolatilityVO> getUnderlyingVolatility(Integer varietyId) {
        List<UnderlyingManager> underlyingManagers = underlyingManagerMapper.selectList(new LambdaQueryWrapper<UnderlyingManager>()
                .eq(UnderlyingManager::getVarietyId, varietyId)
                .isNotNull(UnderlyingManager::getMainContract)
                .eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO)
                .eq(UnderlyingManager::getUnderlyingState, UnderlyingState.Live));
       BigDecimal dividend= systemConfigUtil.getDividendYield();
        return CglibUtil.copyList(underlyingManagers, UnderlyingVolatilityVO::new, (db, vo) -> {
            if (db.getDividendYield() == null) {
                vo.setDividendYield(dividend);
            }
        });
    }

    @Override
    public Page<UnderlyingManagerVO> queryUnderlyingList(UnderlyingManagerPageQueryDto pageQueryDto) {
        Map<Integer, String> varietyMap = varietyService.list(new LambdaQueryWrapper<Variety>()
                        .eq(Variety::getIsDeleted, IsDeletedEnum.NO))
                .stream().collect(Collectors.toMap(Variety::getId, Variety::getVarietyName));

        LambdaQueryWrapper<UnderlyingManager> queryWrapper = new LambdaQueryWrapper<>();
        //交易所
        queryWrapper.eq(StringUtils.isNotBlank(pageQueryDto.getExchange()), UnderlyingManager::getExchange, pageQueryDto.getExchange());
        //标的资产代码
        queryWrapper.like(StringUtils.isNotBlank(pageQueryDto.getUnderlyingCode()), UnderlyingManager::getUnderlyingCode, pageQueryDto.getUnderlyingCode());
        //品种类型
        queryWrapper.in(pageQueryDto.getVarietyIdList() != null && !pageQueryDto.getVarietyIdList().isEmpty(), UnderlyingManager::getVarietyId, pageQueryDto.getVarietyIdList());
        //标的到期日
        queryWrapper.ge(pageQueryDto.getExpireDateStart() != null, UnderlyingManager::getExpireDate, pageQueryDto.getExpireDateStart());
        queryWrapper.le(pageQueryDto.getExpireDateEnd() != null, UnderlyingManager::getExpireDate, pageQueryDto.getExpireDateEnd());
        //是否禁用
        queryWrapper.eq(pageQueryDto.getIsEnabled() != null, UnderlyingManager::getEnabled, pageQueryDto.getIsEnabled());
        //标的状态
        queryWrapper.eq(pageQueryDto.getUnderlyingState() != null, UnderlyingManager::getUnderlyingState, pageQueryDto.getUnderlyingState());
        queryWrapper.eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.eq(pageQueryDto.getIsRiskWarning() != null, UnderlyingManager::getIsRiskWarning, pageQueryDto.getIsRiskWarning());
        Page<UnderlyingManager> dbPage = this.page(new Page<>(pageQueryDto.getPageNo(), pageQueryDto.getPageSize()), queryWrapper);
        //转换数据
        Page<UnderlyingManagerVO> voPage = new Page<>();
        BeanUtil.copyProperties(dbPage, voPage);
        List<UnderlyingManager> records = dbPage.getRecords();
        List<UnderlyingManagerVO> voList = CglibUtil.copyList(records, UnderlyingManagerVO::new, (db, vo) -> {
            if (null != db.getUnderlyingState()) {
                vo.setUnderlyingStateName(db.getUnderlyingState().getDesc());
            }
            vo.setVarietyName(varietyMap.get(db.getVarietyId()));
        });
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public String addUnderlying(UnderlyingManagerAddDto addDto) {
        UnderlyingManager underlyingManager = CglibUtil.copy(addDto, UnderlyingManager.class);
        LambdaQueryWrapper<UnderlyingManager> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(addDto.getUnderlyingCode())) {
            lambdaQueryWrapper.eq(UnderlyingManager::getIsDeleted,IsDeletedEnum.NO);
            lambdaQueryWrapper.eq(UnderlyingManager::getUnderlyingCode,addDto.getUnderlyingCode());
            long count = underlyingManagerMapper.selectCount(lambdaQueryWrapper);
            if (count>0){
                BussinessException.E_600103.assertTrue(false,"合约代码已存在，不允许重复录入相同的合约代码");
            }
        }
        int c = this.getBaseMapper().insert(underlyingManager);
        if (c > 0) {
            return "新增成功";
        } else {
            return "新增失败";
        }
    }

    @Override
    public String editUnderlying(UnderlyingManagerEditDto editDto) {
        UnderlyingManager underlyingManager = CglibUtil.copy(editDto, UnderlyingManager.class);
        int c = this.getBaseMapper().updateById(underlyingManager);
        stringRedisTemplate.delete(RedisAdapter.UNDERLYING_BY_CODE + underlyingManager.getUnderlyingCode());
        if (c > 0) {
            return "保存成功";
        } else {
            return "保存失败";
        }
    }

    @Override
    public String updateUnderlyingEnable(UnderlyingManagerEnableDto enableDto) {
        UnderlyingManager underlyingManager = new UnderlyingManager();
        underlyingManager.setEnabled(enableDto.getEnabled().getFlag());
        underlyingManager.setId(enableDto.getId());
        this.getBaseMapper().updateById(underlyingManager);
        stringRedisTemplate.delete(RedisAdapter.UNDERLYING_BY_CODE + underlyingManagerMapper.selectById(enableDto.getId()).getUnderlyingCode());
        return "更新成功";
    }

    @Override
    public void updateUnderlyingUpDownLimit(Integer varietyId, BigDecimal upDownLimit) {
        //更新条件
        LambdaQueryWrapper<UnderlyingManager> lambdaQueryWrapper= new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UnderlyingManager::getVarietyId,varietyId);
        lambdaQueryWrapper.eq(UnderlyingManager::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(UnderlyingManager::getUnderlyingState,UnderlyingState.Live);
        //删除Redis信息
        List<UnderlyingManager> list=this.list(lambdaQueryWrapper);
        for (UnderlyingManager underlying : list){
            stringRedisTemplate.delete(RedisAdapter.UNDERLYING_BY_CODE + underlying.getUnderlyingCode());
        }
    }

    @Override
    public Set<String> disableList() {
        LambdaQueryWrapper<UnderlyingManager> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO)
                .eq(UnderlyingManager::getIsRiskWarning, 1);
        List<UnderlyingManager> list = underlyingManagerMapper.selectList(lambdaQueryWrapper);
        return list.stream().map(UnderlyingManager::getUnderlyingCode).collect(Collectors.toSet());
    }

    @Override
    public List<UnderlyingManagerVO> getMainUnderlyingList() {
        List<UnderlyingManager> underlyingManagers = underlyingManagerMapper.selectList(new LambdaQueryWrapper<UnderlyingManager>()
                .eq(UnderlyingManager::getMainContract,MainContractEnum.yes)
                .eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO)
                .eq(UnderlyingManager::getUnderlyingState, UnderlyingState.Live));

        return CglibUtil.copyList(underlyingManagers, UnderlyingManagerVO::new, (db, vo) -> {
            if (db.getDividendYield() == null) {
                vo.setDividendYield(systemConfigUtil.getDividendYield());
            }
        });
    }

    @Override
    public List<UnderlyingManagerVO> getUnderlyingByVarietyList(List<Integer> varietyIdList, LocalDate queryDate) {
        List<UnderlyingManager> underlyingManagers = underlyingManagerMapper.selectList(new LambdaQueryWrapper<UnderlyingManager>()
                .in(UnderlyingManager::getVarietyId, varietyIdList)
                .eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO)
                .ge(UnderlyingManager::getExpireDate,queryDate)
                .le(UnderlyingManager::getCreateDate,queryDate)
        );
        return CglibUtil.copyList(underlyingManagers,UnderlyingManagerVO::new);
    }
}
