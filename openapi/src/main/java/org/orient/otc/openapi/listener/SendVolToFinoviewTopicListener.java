package org.orient.otc.openapi.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.orient.otc.api.finoview.dto.VolatilityDTO;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.openapi.service.FinoviewService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 同步处理波动率同步消息
 * @author dzrh
 */
@RocketMQMessageListener(topic = RocketMqConstant.SYNC_TOPIC
        , selectorType = SelectorType.TAG
        , selectorExpression = RocketMqConstant.VOL_TO_FINOVIEW
        , consumerGroup = RocketMqConstant.VOL_TO_FINOVIEW
        , consumeMode = ConsumeMode.CONCURRENTLY)
@Component
@Slf4j
public class SendVolToFinoviewTopicListener implements RocketMQListener<List<Object>>, RocketMQPushConsumerLifecycleListener {
    @Resource
    FinoviewService finoviewService;

    @Override
    public void onMessage(List<Object> objectListlist) {
        if (log.isDebugEnabled()){
            log.debug("同步繁微波动率内容为:{}",objectListlist.toString());
        }
        List<VolatilityDTO> list = JSONObject.parseArray(objectListlist.toString(),VolatilityDTO.class);
        finoviewService.sendVolToFinoview(list);
    }
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setInstanceName(RocketMqConstant.SYNC_TOPIC + "-" + RocketMqConstant.VOL_TO_FINOVIEW);
    }
}
