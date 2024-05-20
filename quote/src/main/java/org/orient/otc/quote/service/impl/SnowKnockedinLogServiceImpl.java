package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.quote.dto.SnowKnockedinLogPageDto;
import org.orient.otc.quote.entity.SnowKnockedinLog;
import org.orient.otc.quote.mapper.SnowKnockedinLogMapper;
import org.orient.otc.quote.service.SnowKnockedinLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SnowKnockedinLogServiceImpl extends ServiceImpl<BaseMapper<SnowKnockedinLog>, SnowKnockedinLog> implements SnowKnockedinLogService {

    @Autowired
    SnowKnockedinLogMapper snowKnockedinLogMapper;

    @Override
    public IPage<SnowKnockedinLog> selectListByPage(SnowKnockedinLogPageDto dto) {
        LambdaQueryWrapper<SnowKnockedinLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dto.getKnockedInDate()!=null,SnowKnockedinLog::getKnockedInDate,dto.getKnockedInDate());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(dto.getUnderlyingCode()),SnowKnockedinLog::getUnderlyingCode,dto.getUnderlyingCode());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(dto.getTradeCode()),SnowKnockedinLog::getTradeCode,dto.getTradeCode());
        lambdaQueryWrapper.eq(dto.getOptionType()!=null,SnowKnockedinLog::getOptionType,dto.getOptionType());
        if (dto.getKnockinBarrierValueStart() != null && dto.getKnockinBarrierValueEnd() != null) {
            lambdaQueryWrapper.ge(SnowKnockedinLog::getKnockinBarrierValue,dto.getKnockinBarrierValueStart());
            lambdaQueryWrapper.le(SnowKnockedinLog::getKnockinBarrierValue,dto.getKnockinBarrierValueEnd());
        }
        if (dto.getClosePriceStart() != null && dto.getClosePriceEnd() != null) {
            lambdaQueryWrapper.ge(SnowKnockedinLog::getClosePrice,dto.getClosePriceStart());
            lambdaQueryWrapper.le(SnowKnockedinLog::getClosePrice,dto.getClosePriceEnd());
        }
        lambdaQueryWrapper.orderByDesc(SnowKnockedinLog::getCreateTime);
        return this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
    }
}
