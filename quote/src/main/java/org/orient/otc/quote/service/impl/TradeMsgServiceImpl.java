package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.trade.TradeMsgQueryDto;
import org.orient.otc.quote.entity.TradeMsg;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.service.TradeMsgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author dzrh
 */
@Service
public class TradeMsgServiceImpl extends ServiceImpl<BaseMapper<TradeMsg>, TradeMsg> implements TradeMsgService {

    @Override
    public TradeMsg queryMsgInfo(TradeMsgQueryDto tradeMsgQueryDto) {

        LambdaQueryWrapper<TradeMsg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeMsg::getTradeId,tradeMsgQueryDto.getTradeId());
        queryWrapper.eq(TradeMsg::getTradeType,tradeMsgQueryDto.getTradeType());
        queryWrapper.eq(TradeMsg::getIsDeleted, IsDeletedEnum.NO);
        TradeMsg tradeMsg =this.getOne(queryWrapper);
        BussinessException.E_300401.assertTrue(Objects.nonNull(tradeMsg));
        return tradeMsg;
    }

    @Override
    @Transactional
    public boolean saveOrUpdateBatchByTradeId(List<TradeMsg> entityList) {
        for (TradeMsg tradeMsg : entityList){
            LambdaQueryWrapper<TradeMsg> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(TradeMsg::getTradeId,tradeMsg.getTradeId());
            queryWrapper.eq(TradeMsg::getTradeType,tradeMsg.getTradeType());
            if ( Objects.isNull(this.getOne(queryWrapper))){
                this.save(tradeMsg);
            }else {
                this.update(tradeMsg, queryWrapper);
            }
        }
        return true;
    }
}
