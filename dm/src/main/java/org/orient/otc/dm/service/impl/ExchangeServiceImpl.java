package org.orient.otc.dm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.dm.dto.ExchangeDeleteDto;
import org.orient.otc.dm.dto.ExchangePageDto;
import org.orient.otc.dm.dto.ExchangeSaveDto;
import org.orient.otc.dm.entity.Exchange;
import org.orient.otc.dm.mapper.ExchangeMapper;
import org.orient.otc.dm.service.ExchangeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * (Exchange)表服务实现类
 * @author makejava
 * @since 2023-07-20 13:44:58
 */
@Service("exchangeService")
public class ExchangeServiceImpl extends ServiceImpl<ExchangeMapper, Exchange> implements ExchangeService {

    @Resource
    ExchangeMapper exchangeMapper;

    @Override
    public IPage<Exchange> selectListByPage(ExchangePageDto dto) {
        LambdaQueryWrapper<Exchange> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!StringUtils.isEmpty(dto.getName()),Exchange::getName,dto.getName())
                .eq(!StringUtils.isEmpty(dto.getCode()),Exchange::getCode,dto.getCode())
                .like(!StringUtils.isEmpty(dto.getShortname()),Exchange::getShortname,dto.getShortname())
                .eq(Exchange::getIsDeleted,0)
        ;
        return this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
    }

    @Override
    public String saveExchange(ExchangeSaveDto dto) {
        Exchange exchange = new Exchange();
        BeanUtils.copyProperties(dto,exchange);
        this.saveOrUpdate(exchange);
        return "exchange save success";
    }

    @Override
    public String deleteExchange(ExchangeDeleteDto dto) {
        LambdaUpdateWrapper<Exchange> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Exchange::getId,dto.getId())
        .set(Exchange::getIsDeleted,1);
        this.update(lambdaUpdateWrapper);
        return "exchange delete success";
    }
}

