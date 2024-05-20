package org.orient.otc.yl.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.orient.otc.api.quote.vo.CapitalRecordsVO;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.yl.service.YlService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 同步处理波动率同步消息
 * @author dzrh
 */
@RocketMQMessageListener(topic = RocketMqConstant.SYNC_TOPIC
        , selectorType = SelectorType.TAG
        , selectorExpression = RocketMqConstant.CAPITAL_CONFIRMED
        , consumerGroup = RocketMqConstant.CAPITAL_CONFIRMED
        , consumeMode = ConsumeMode.CONCURRENTLY)
@Component
@Slf4j
public class SyncTopicCapitalConfirmedListener implements RocketMQListener<CapitalRecordsVO>, RocketMQPushConsumerLifecycleListener {
    @Resource
    private YlService ylService;
    @Override
    public void onMessage(CapitalRecordsVO capitalRecordsVO) {
        if (log.isDebugEnabled()) {
            log.debug("同步镒链资金记录内容为:{}", JSONObject.toJSONString(capitalRecordsVO));
        }
        //推送确认记录
        ylService.clientCashInCashOutConfirmed(capitalRecordsVO.getYlId());
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setInstanceName(RocketMqConstant.SYNC_TOPIC + "-" + RocketMqConstant.CAPITAL_CONFIRMED);
    }
}
