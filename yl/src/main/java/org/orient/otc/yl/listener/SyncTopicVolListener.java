package org.orient.otc.yl.listener;

import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.orient.otc.api.quote.dto.VolatityDataDto;
import org.orient.otc.api.quote.dto.VolatitySaveDto;
import org.orient.otc.api.quote.enums.VolTypeEnum;
import org.orient.otc.yl.entity.SingleVol;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.yl.dto.SaveVolatilityDto;
import org.orient.otc.yl.service.YlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 同步处理波动率同步消息
 * @author dzrh
 */
@RocketMQMessageListener(topic = RocketMqConstant.SYNC_TOPIC
        ,selectorType = SelectorType.TAG
        ,selectorExpression = RocketMqConstant.SYNC_TOPIC_VOL
        ,consumerGroup = RocketMqConstant.SYNC_TOPIC_VOL
        ,consumeMode= ConsumeMode.CONCURRENTLY)
@Component
@Slf4j
public class SyncTopicVolListener implements RocketMQListener<List<Object>>, RocketMQPushConsumerLifecycleListener {
    @Autowired
    YlService ylService;
    @Override
    public void onMessage(List<Object> objectListlist) {
        if (log.isDebugEnabled()){
            log.debug("同步镒链波动率内容为:{}",objectListlist.toString());
        }
        List<VolatitySaveDto> list = JSONObject.parseArray(objectListlist.toString(),VolatitySaveDto.class);
        VolatitySaveDto tempMidVol = list.stream().filter(v->v.getVolType() == VolTypeEnum.mid)
                .findFirst().orElseThrow(()->new RuntimeException("找不到对应的MidVol"));
        VolatitySaveDto midVol=JSONObject.parseObject(JSONObject.toJSONString(tempMidVol),VolatitySaveDto.class);
        for (VolatitySaveDto volInfo : list) {
            SaveVolatilityDto saveVolatilityDto = new SaveVolatilityDto();
            saveVolatilityDto.setContractCode(volInfo.getUnderlyingCode());
            saveVolatilityDto.setQuotationDate(volInfo.getQuotationDate().toString());
            saveVolatilityDto.setVolType(volInfo.getVolType().getDesc());
            saveVolatilityDto.setVolTable(CglibUtil.copyList(convertVol(midVol.getData(),volInfo.getData()), SingleVol::new, (s, t) -> {
                t.setExpire(s.getExpire() + "D");
//                t.setVol(s.getVol().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            }));

            ylService.saveVolatility(saveVolatilityDto);
        }
    }

    private List<VolatityDataDto> convertVol(List<VolatityDataDto> midList, List<VolatityDataDto> targetList) {
        for (int i = 0; i < targetList.size(); i++) {
            VolatityDataDto target = targetList.get(i);
            VolatityDataDto mid = midList.get(i);
            if (mid.getVol().compareTo(target.getVol())==0){
                target.setVol(mid.getVol().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            }else {
                target.setVol(mid.getVol().add(target.getVol()).divide(new BigDecimal("100"),4, RoundingMode.HALF_UP));
            }
        }
        return targetList;
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setInstanceName(RocketMqConstant.SYNC_TOPIC + "-" + RocketMqConstant.SYNC_TOPIC_VOL);
    }
}
