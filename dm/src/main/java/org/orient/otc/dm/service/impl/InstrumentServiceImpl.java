package org.orient.otc.dm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.dm.dto.InstrumentPageDto;
import org.orient.otc.dm.dto.InstrumentUpdateDto;
import org.orient.otc.dm.dto.QueryInstrumentPage;
import org.orient.otc.dm.entity.Instrument;
import org.orient.otc.dm.mapper.InstrumentMapper;
import org.orient.otc.dm.service.InstrumentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 场内合约服务实现
 */
@RefreshScope
@Service
public class InstrumentServiceImpl  extends ServiceImpl<BaseMapper<Instrument>, Instrument> implements InstrumentService {
    @Resource
    private  InstrumentMapper instrumentMapper;

    @Value("#{'${productClass}'.split(',')}")
    private List<Integer> productClassList;
    @Override
    public Page<Instrument> getListByPage(QueryInstrumentPage queryInstrumentPage) {
        LambdaQueryWrapper<Instrument> queryWrapper =   new LambdaQueryWrapper<>();
        queryWrapper.in(Instrument::getProductClass,productClassList);
        queryWrapper.eq(Instrument::getIsDeleted, IsDeletedEnum.NO);
        Page<Instrument> page = new Page<>();

        page.setCurrent(queryInstrumentPage.getPageNo());
        page.setSize(queryInstrumentPage.getPageSize());
        return instrumentMapper.selectPage(page,queryWrapper);
    }

    @Override
    public InstrumentInfoVo getInstrumentInfo(String instID) {
        LambdaQueryWrapper<Instrument> instrumentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        instrumentLambdaQueryWrapper.eq(Instrument :: getInstrumentId, instID);
        return this.getVoOne(instrumentLambdaQueryWrapper,InstrumentInfoVo.class);
    }

    @Override
    public List<InstrumentInfoVo> getInstrumentInfoByIds(Set<String> instIDs) {
        if (instIDs.isEmpty()){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Instrument> instrumentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        instrumentLambdaQueryWrapper.in(Instrument :: getInstrumentId, instIDs);
        instrumentLambdaQueryWrapper.eq(Instrument :: getIsDeleted, IsDeletedEnum.NO);
        //return this.getVoOne(instrumentLambdaQueryWrapper,InstrumentInfoVo.class);
        List<Instrument> list = instrumentMapper.selectList(instrumentLambdaQueryWrapper);
        return list.stream().map(item->{
            InstrumentInfoVo vo = new InstrumentInfoVo();
            BeanUtils.copyProperties(item,vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<InstrumentInfoVo> getInstrumentInfoByUndeingCodes(Set<String> codes) {
        LambdaQueryWrapper<Instrument> instrumentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        instrumentLambdaQueryWrapper.in(Instrument :: getUnderlyingInstrId, codes)
                .or(wrapper->wrapper.in(Instrument::getInstrumentId,codes));
        instrumentLambdaQueryWrapper.eq(Instrument::getIsDeleted,IsDeletedEnum.NO);
        List<Instrument> list = instrumentMapper.selectList(instrumentLambdaQueryWrapper);
        return list.stream().map(item->{
            InstrumentInfoVo vo = new InstrumentInfoVo();
            BeanUtils.copyProperties(item,vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public String updateInstrument(InstrumentUpdateDto dto) throws Exception {
        Instrument db = instrumentMapper.selectById(dto.getInstrumentId());
        if (db == null) {
            throw new Exception("未查询到相关合约,合约代码="+dto.getInstrumentId());
        }
        Instrument entity = new Instrument();
        BeanUtils.copyProperties(dto,entity);
        this.saveOrUpdate(entity);
        return "操作成功";
    }

    @Override
    public Page<Instrument> selectListByPage(InstrumentPageDto dto) {
        LambdaQueryWrapper<Instrument> queryWrapper =   new LambdaQueryWrapper<>();
        queryWrapper.eq(Instrument::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.likeRight(StringUtils.isNotBlank(dto.getInstrumentId()),Instrument::getInstrumentId,dto.getInstrumentId());
        queryWrapper.eq(StringUtils.isNotBlank(dto.getExchangeId()),Instrument::getExchangeId,dto.getExchangeId());
        queryWrapper.eq(dto.getProductClass() != null,Instrument::getProductClass,dto.getProductClass());
        queryWrapper.in(CollectionUtils.isNotEmpty(dto.getProductIdList()),Instrument::getProductId,dto.getProductIdList());
        queryWrapper.in(CollectionUtils.isNotEmpty(dto.getInstLifePhaseList()),Instrument::getInstLifePhase,dto.getInstLifePhaseList());
        queryWrapper.in(CollectionUtils.isNotEmpty(dto.getIsTradingList()),Instrument::getIsTrading,dto.getIsTradingList());
        queryWrapper.in(CollectionUtils.isNotEmpty(dto.getOptionsTypeList()),Instrument::getOptionsType,dto.getOptionsTypeList());
        queryWrapper.eq(StringUtils.isNotBlank(dto.getUnderlyingInstrId()),Instrument::getUnderlyingInstrId,dto.getUnderlyingInstrId());
        queryWrapper.ge(StringUtils.isNotBlank(dto.getExpireDateStart()),Instrument::getExpireDate,dto.getExpireDateStart());
        queryWrapper.le(StringUtils.isNotBlank(dto.getExpireDateEnd()),Instrument::getExpireDate,dto.getExpireDateEnd());
        return this.page(new Page(dto.getPageNo(),dto.getPageSize()),queryWrapper);
    }
}
