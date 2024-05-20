package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.feign.InstrumentClient;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.api.user.feign.ExchangeAccountClient;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.trade.ExchangePositionCheckPageListDto;
import org.orient.otc.quote.entity.ExchangePositionCheck;
import org.orient.otc.quote.service.ExchangePositionCheckService;
import org.orient.otc.quote.vo.ExchangePositionCheckVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExchangePositionCheckServiceImpl extends ServiceImpl<BaseMapper<ExchangePositionCheck>, ExchangePositionCheck> implements ExchangePositionCheckService {

    @Autowired
    ExchangeAccountClient exchangeAccountClient;

    @Autowired
    InstrumentClient instrumentClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public IPage<ExchangePositionCheckVo> selectOptionListByPage(ExchangePositionCheckPageListDto dto) {
        LambdaQueryWrapper<ExchangePositionCheck> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(dto.getTradingDay())) {
            lambdaQueryWrapper.eq(ExchangePositionCheck::getTradingDay,dto.getTradingDay());
        }else {
            lambdaQueryWrapper.eq(ExchangePositionCheck::getTradingDay, LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO
                    , SystemConfigEnum.tradeDay.name())).toString()));
        }

        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getInvestorIds()),ExchangePositionCheck::getInvestorId,dto.getInvestorIds())
        .eq(!StringUtils.isEmpty(dto.getInstrumentId()),ExchangePositionCheck::getInstrumentId,dto.getInstrumentId())
        .eq(Objects.nonNull(dto.getPosiDirection()),ExchangePositionCheck::getPosiDirection,dto.getPosiDirection())
        .eq(!StringUtils.isEmpty(dto.getStatus()),ExchangePositionCheck::getStatus,dto.getStatus())
        .eq(ExchangePositionCheck::getIsDeleted, IsDeletedEnum.NO);
        IPage<ExchangePositionCheck> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        Set<String> accountSet = ipage.getRecords().stream().map(ExchangePositionCheck::getInvestorId).collect(Collectors.toSet());
        log.debug("获取场内账号列表="+accountSet.size());
        // 获取场内账号名称
        Map<String,String> accountMap = getExchangeAccountMap(accountSet);
        Set<String> instrumentSet = ipage.getRecords().stream().map(ExchangePositionCheck::getInstrumentId).collect(Collectors.toSet());
        log.debug("合约代码列表="+instrumentSet.size());
        // 获取合约名称
        Map<String,String> instrumentMap = getInstrumentMap(instrumentSet);
        return ipage.convert(item->{
            ExchangePositionCheckVo vo = new ExchangePositionCheckVo();
            BeanUtils.copyProperties(item,vo);

            if (null != item.getPosiDirection()) {
                String posDirection = "";
                if (item.getPosiDirection()==2) {
                    posDirection = "多头";
                }else if (item.getPosiDirection()==3) {
                    posDirection = "空头";
                }
                vo.setPosiDirection(posDirection);
            }
            if (null != item.getStatus()) {
                vo.setStatus(item.getStatus().getDesc());
            }
            vo.setInvestorName(accountMap.get(item.getInvestorId()));
            vo.setInstrumentName(instrumentMap.get(item.getInstrumentId()));
            return vo;
        });
    }

    /**
     * 获取场内账号对应的名称
     * key = 场内账号 value = 名称
     * @param accounts
     * @return
     */
    public Map<String,String> getExchangeAccountMap(Set<String> accounts) {
        if (CollectionUtils.isEmpty(accounts)) {
            return  new HashMap<>();
        }
        List<ExchangeAccountFeignVO> list = exchangeAccountClient.getVoByAccounts(accounts);
        return list.stream().collect(Collectors.toMap(ExchangeAccountFeignVO::getAccount, ExchangeAccountFeignVO::getName,(v1, v2)->v2));
    }

    /**
     * 获取合约代码对应的合约名称
     * key = 场内账号 value = 名称
     * @param instrumentIds
     * @return
     */
    public Map<String,String> getInstrumentMap(Set<String> instrumentIds){
        if (CollectionUtils.isEmpty(instrumentIds)) {
            return  new HashMap<>();
        }
        List<InstrumentInfoVo> list = instrumentClient.getInstrumentInfoByIds(instrumentIds);
        return list.stream().collect(Collectors.toMap(InstrumentInfoVo::getInstrumentId, InstrumentInfoVo::getInstrumentName,(v1, v2)->v2));
    }
}
