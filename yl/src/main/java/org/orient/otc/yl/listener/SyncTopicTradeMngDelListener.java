package org.orient.otc.yl.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.yl.dto.TradeDelSyncDto;
import org.orient.otc.yl.service.SyncServe;
import org.orient.otc.yl.vo.TradeMngByYlVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 同步处理波动率同步消息
 * @author dzrh
 */
@RocketMQMessageListener(topic = RocketMqConstant.SYNC_TOPIC
        , selectorType = SelectorType.TAG
        , selectorExpression = RocketMqConstant.SYNC_TOPIC_TRADE_MNG_DEL
        , consumerGroup = RocketMqConstant.SYNC_TOPIC_TRADE_MNG_DEL
        , consumeMode = ConsumeMode.CONCURRENTLY)
@Component
@Slf4j
public class SyncTopicTradeMngDelListener implements RocketMQListener<List<Object>>, RocketMQPushConsumerLifecycleListener {
    @Resource
    SyncServe syncServe;

    @Override
    public void onMessage(List<Object> objectListlist) {
        List<TradeMngByYlVo> notSyncList = JSONArray.parseArray(objectListlist.toString(), TradeMngByYlVo.class);
        log.info("读取到删除交易信息:{}", JSONObject.toJSONString(notSyncList));
        Map<Boolean,List<TradeMngByYlVo>> notSyncMap=notSyncList.stream().collect(Collectors.groupingBy(vo->vo.getOptionCombType()==null));
        //单腿同步
        if (notSyncMap.get(Boolean.TRUE) != null) {
            for (TradeMngByYlVo vo : notSyncMap.get(Boolean.TRUE)) {
                TradeDelSyncDto tradeDelSyncDto = new TradeDelSyncDto();
                tradeDelSyncDto.setTradeIdList(Collections.singletonList(vo.getId()));
                tradeDelSyncDto.setTradeNumber(vo.getTradeCode());
                syncServe.syncTradeDel(tradeDelSyncDto);
            }
        }
        //组合同步
        if (notSyncMap.get(Boolean.FALSE) != null) {
            for (List<TradeMngByYlVo> voList : notSyncMap.get(Boolean.FALSE).stream().collect(Collectors.groupingBy(TradeMngByYlVo::getCombCode)).values()) {
                TradeDelSyncDto tradeDelSyncDto = new TradeDelSyncDto();
                tradeDelSyncDto.setTradeIdList(voList.stream().map(TradeMngByYlVo::getId).collect(Collectors.toList()));
                tradeDelSyncDto.setTradeNumber(voList.get(0).getTradeCode());
                syncServe.syncTradeDel(tradeDelSyncDto);
            }
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setInstanceName(RocketMqConstant.SYNC_TOPIC + "-" + RocketMqConstant.SYNC_TOPIC_TRADE_MNG_DEL);
    }
}
