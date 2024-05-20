package org.orient.otc.quote.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.dm.feign.InstrumentClient;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.api.user.feign.ExchangeAccountClient;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.risk.ExchangeRealTimePos;
import org.orient.otc.quote.entity.ExchangePosition;
import org.orient.otc.quote.entity.ExchangeTrade;
import org.orient.otc.quote.mapper.ExchangeTradeMapper;
import org.orient.otc.quote.service.ExchangePositionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExchangePositionServiceImpl extends ServiceImpl<BaseMapper<ExchangePosition>, ExchangePosition> implements ExchangePositionService {

    @Resource
    private ExchangeTradeMapper exchangeTradeMapper;
    @Resource
    private InstrumentClient instrumentClient;

    @Resource
    private ExchangeAccountClient exchangeAccountClient;

    @Override
    public List<ExchangeRealTimePos> selectPositionByTradingDay(String tradingDay) {
        LambdaQueryWrapper<ExchangePosition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExchangePosition::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.eq(ExchangePosition::getTradingDay, tradingDay);
        return getExchangeRealTimePos(queryWrapper);
    }

    @Override
    public List<ExchangeRealTimePos> selectPositionBySupplementaryAndTradingDay(String tradingDay) {

        LambdaQueryWrapper<ExchangeTrade> tradeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeLambdaQueryWrapper.eq(ExchangeTrade::getTradingDay, tradingDay);
        tradeLambdaQueryWrapper.like(ExchangeTrade::getOrderSysID, "\\_");
        tradeLambdaQueryWrapper.eq(ExchangeTrade::getIsDeleted, 0);
        List<ExchangeTrade> tradeList = exchangeTradeMapper.selectList(tradeLambdaQueryWrapper);
        Set<String> instrumentIdSet = tradeList.stream().map(ExchangeTrade::getInstrumentID).collect(Collectors.toSet());
        LambdaQueryWrapper<ExchangePosition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExchangePosition::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.eq(ExchangePosition::getTradingDay, tradingDay);
        queryWrapper.in(ExchangePosition::getInstrumentID,instrumentIdSet);
        return getExchangeRealTimePos(queryWrapper);
    }

    @NonNull
    private List<ExchangeRealTimePos> getExchangeRealTimePos(LambdaQueryWrapper<ExchangePosition> queryWrapper) {
        List<ExchangePosition> list = this.list(queryWrapper);
        List<ExchangeRealTimePos> realTimePosList = new ArrayList<>();
        Set<String> instrumentSet = list.stream().map(ExchangePosition::getInstrumentID).collect(Collectors.toSet());
        List<InstrumentInfoVo> instrumentInfoVoList = instrumentClient.getInstrumentInfoByIds(instrumentSet);
        Map<String, InstrumentInfoVo> instrumentInfoVoMap = instrumentInfoVoList.stream().collect(Collectors.toMap(InstrumentInfoVo::getInstrumentId, item -> item, (v1, v2) -> v2));
        List<ExchangeAccountFeignVO> accountFeignVOList = exchangeAccountClient.getList();
        Map<String, ExchangeAccountFeignVO> accountFeignVOMap = accountFeignVOList.stream().collect(Collectors.toMap(ExchangeAccountFeignVO::getAccount, item -> item, (v1, v2) -> v2));
        for (ExchangePosition posData : list) {
            ExchangeRealTimePos pos = CglibUtil.copy(posData, ExchangeRealTimePos.class);
            if (pos.getTradeCost() == null) {
                pos.setTradeCost(BigDecimal.ZERO);
            }
            //簿记信息
            ExchangeAccountFeignVO accountFeignVO=  accountFeignVOMap.get(pos.getInvestorID());
            pos.setAssetId(accountFeignVO.getAssetunitId());
            //查询期货合约信息(微服务内部请求)，设置合约的基本信息
            InstrumentInfoVo instInfo = instrumentInfoVoMap.get(posData.getInstrumentID());
            pos.setUnderlyingCode(instInfo.getOptionsType() == 0 ? instInfo.getInstrumentId().toUpperCase() : instInfo.getUnderlyingInstrId().toUpperCase());
            pos.setExpireDate(instInfo.getExpireDate());
            pos.setOptionsType(instInfo.getOptionsType());
            pos.setStrikePrice(instInfo.getStrikePrice());
            realTimePosList.add(pos);
        }
        return realTimePosList;
    }
}
