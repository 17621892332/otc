package org.orient.otc.yl.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.orient.otc.api.quote.vo.TradeCloseMngFeignVo;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.yl.service.SyncServe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 同步处理波动率同步消息
 * @author dzrh
 */
@RocketMQMessageListener(topic = RocketMqConstant.SYNC_TOPIC
        , selectorType = SelectorType.TAG
        , selectorExpression = RocketMqConstant.SYNC_TOPIC_TRADE_CLOSE_MNG_CLOSE
        , consumerGroup = RocketMqConstant.SYNC_TOPIC_TRADE_CLOSE_MNG_CLOSE
        , consumeMode = ConsumeMode.CONCURRENTLY)
@Component
@Slf4j
public class SyncTopicTradeCloseMngListener implements RocketMQListener<List<Object>>, RocketMQPushConsumerLifecycleListener {
    @Autowired
    SyncServe syncServe;

    @Override
    public void onMessage(List<Object> objectListlist) {
        List<TradeCloseMngFeignVo> notSyncList = JSONObject.parseArray(objectListlist.toString(), TradeCloseMngFeignVo.class);
        log.info("读取到平仓信息:{}",JSONObject.toJSONString(notSyncList));
        for (TradeCloseMngFeignVo vo:notSyncList) {
            syncServe.syncTradeCloseToYl(vo,"平仓");
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setInstanceName(RocketMqConstant.SYNC_TOPIC + "-" + RocketMqConstant.SYNC_TOPIC_TRADE_CLOSE_MNG_CLOSE);
    }
}
