package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.quote.dto.SettlementTradeObsDateDTO;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.entity.TradeMng;
import org.orient.otc.quote.entity.TradeObsDate;
import org.orient.otc.quote.mapper.TradeMngMapper;
import org.orient.otc.quote.mapper.TradeObsDateMapper;
import org.orient.otc.quote.service.TradeObsDateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 观察日服务实现
 */
@Service
@Slf4j
public class TradeObsDateServiceImpl implements TradeObsDateService {
    @Resource
    TradeObsDateMapper tradeObsDateMapper;

    @Resource
    TradeMngMapper tradeMngMapper;

    @Override
    public List<String> getNeedKnockOutTradeCodeList(SettlementTradeObsDateDTO dto) {
        List<String> returnList = new ArrayList<>();
        LambdaQueryWrapper<TradeMng> tradeMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeMngLambdaQueryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        tradeMngLambdaQueryWrapper.in(TradeMng::getOptionType, OptionTypeEnum.getHaveKnockOut());
        // 状态为存活
        tradeMngLambdaQueryWrapper.in(TradeMng::getTradeState, TradeStateEnum.getLiveStateList());
        List<TradeMng> tradeMngList = tradeMngMapper.selectList(tradeMngLambdaQueryWrapper);
        if (tradeMngList != null && !tradeMngList.isEmpty()) {
            Set<Integer> tradeIds = tradeMngList.stream().map(TradeMng::getId).collect(Collectors.toSet());
            Map<Integer, TradeMng> tradeMngMap = tradeMngList.stream().collect(Collectors.toMap(TradeMng::getId, Function.identity(), (v1, v2) -> v1));
            LambdaQueryWrapper<TradeObsDate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            //按观察日期查询
            lambdaQueryWrapper.eq(TradeObsDate::getObsDate, dto.getSettlementDate());
            lambdaQueryWrapper.in(TradeObsDate::getTradeId, tradeIds);
            lambdaQueryWrapper.eq(TradeObsDate::getIsDeleted, IsDeletedEnum.NO);
            lambdaQueryWrapper.orderByAsc(TradeObsDate::getObsDate);
            List<TradeObsDate> tradeObsDateList = tradeObsDateMapper.selectList(lambdaQueryWrapper);
            for (TradeObsDate item : tradeObsDateList) {
                TradeMng tradeMng = tradeMngMap.get(item.getTradeId());
                boolean flag = false;
                BigDecimal barrier;
                if (OptionTypeEnum.getSnowBall().contains(tradeMng.getOptionType())){
                    if (item.getBarrierRelative() != null && item.getBarrierRelative()) {
                        barrier = tradeMng.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getBarrier()));
                    } else {
                        barrier = item.getBarrier();
                    }
                }else {
                    barrier =tradeMng.getBarrier();
                }
                //敲出价格为0则不需要敲出
                if (barrier.compareTo(BigDecimal.ZERO)==0){
                    continue;
                }
                // 雪球看涨
                if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AISnowBallCallPricer||
                        tradeMng.getOptionType() == OptionTypeEnum.AICallKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AICallFixKOAccPricer ||
                        tradeMng.getOptionType()==OptionTypeEnum.AIEnCallKOAccPricer) {
                    flag = item.getPrice().compareTo(barrier) >= 0;
                }
                // 雪球看跌
                if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AISnowBallPutPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutFixKOAccPricer ||
                        tradeMng.getOptionType()==OptionTypeEnum.AIEnPutKOAccPricer) {
                    flag = item.getPrice().compareTo(barrier) <= 0;
                }
                if (flag) {
                    returnList.add(tradeMng.getTradeCode());
                }
            }
        }
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<TradeObsDate> getTradeObsDateListByTradeIdList(List<Integer> tradeIdList) {
        LambdaQueryWrapper<TradeObsDate> tradeObsDateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeObsDateLambdaQueryWrapper.in(tradeIdList != null&& !tradeIdList.isEmpty(), TradeObsDate::getTradeId, tradeIdList);
        tradeObsDateLambdaQueryWrapper.eq(TradeObsDate::getIsDeleted, IsDeletedEnum.NO);
        tradeObsDateLambdaQueryWrapper.orderByAsc(TradeObsDate::getObsDate);
        return tradeObsDateMapper.selectList(tradeObsDateLambdaQueryWrapper);
    }
}
