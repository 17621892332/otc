package org.orient.otc.market.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.market.vo.MarketInfoVO;
import org.orient.otc.common.redispubsub.RedisPubSubTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.orient.otc.market.adapter.MarketAdapter.marketData;

/**
 * 行情信息订阅
 */
@Component
@Slf4j
public class MyRedisPubSubTemplate implements RedisPubSubTemplate {
    /**
     * 当前时间时间戳
     */
    public static Long time = null;

    @Override
    public void onMessage(String message) {
        //收到消息会调用
        try {
            MarketInfoVO marketInfoVo = JSONObject.parseObject(message, MarketInfoVO.class);

            marketData.put(marketInfoVo.getInstrumentId().toUpperCase(),marketInfoVo);
            if(time != null){
                long now = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
                if( now - time >= 10) {
                    log.info("行情订阅继续中......");
                    time=now;
                }
            }else {
                time = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
            }
        } catch (Exception e) {
            log.error("redis订阅消息的格式不对：" + message, e);
        }
    }

    @Override
    public String setChannel() {
        return "ctp_md";
    }
}
