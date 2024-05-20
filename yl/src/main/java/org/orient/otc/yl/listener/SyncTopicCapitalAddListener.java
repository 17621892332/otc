package org.orient.otc.yl.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.orient.otc.api.quote.dto.CapitalSyncDTO;
import org.orient.otc.api.quote.feign.CapitalClient;
import org.orient.otc.api.quote.vo.CapitalRecordsVO;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.yl.dto.ClientCashInCashOutDTO;
import org.orient.otc.yl.service.YlService;
import org.orient.otc.yl.vo.ClientCashInoCashOutVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 同步处理波动率同步消息
 * @author dzrh
 */
@RocketMQMessageListener(topic = RocketMqConstant.SYNC_TOPIC
        , selectorType = SelectorType.TAG
        , selectorExpression = RocketMqConstant.CAPITAL_ADD
        , consumerGroup = RocketMqConstant.CAPITAL_ADD
        , consumeMode = ConsumeMode.CONCURRENTLY)
@Component
@Slf4j
public class SyncTopicCapitalAddListener implements RocketMQListener<CapitalRecordsVO>, RocketMQPushConsumerLifecycleListener {
    @Resource
    private YlService ylService;
    @Resource
    private CapitalClient capitalClient;

    @Override
    public void onMessage(CapitalRecordsVO capitalRecordsVO) {
        if (log.isDebugEnabled()) {
            log.debug("同步镒链资金记录内容为:{}", JSONObject.toJSONString(capitalRecordsVO));
        }
        //推送镒链资金记录
        ClientCashInCashOutDTO clientCashInCashOutDTO = ClientCashInCashOutDTO.builder()
                .clientId(capitalRecordsVO.getClientId())
                .direction(capitalRecordsVO.getDirection().getDesc())
                .money(capitalRecordsVO.getMoney().abs())
                .openBankCard(capitalRecordsVO.getBankAccount())
                .happenDate(capitalRecordsVO.getVestingDate())
                .comments(capitalRecordsVO.getRemark())
                .build();
        ClientCashInoCashOutVO clientCashInoCashOutVO = ylService.addClientCashInCashOut(clientCashInCashOutDTO);
        //更新镒链状态
        CapitalSyncDTO capitalSyncDTO = new CapitalSyncDTO();
        capitalSyncDTO.setId(capitalRecordsVO.getId());
        capitalSyncDTO.setYlId(clientCashInoCashOutVO.getId());
        capitalSyncDTO.setNumber(clientCashInoCashOutVO.getNumber());
        capitalClient.updateSync(capitalSyncDTO);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setInstanceName(RocketMqConstant.SYNC_TOPIC + "-" + RocketMqConstant.CAPITAL_ADD);
    }
}
