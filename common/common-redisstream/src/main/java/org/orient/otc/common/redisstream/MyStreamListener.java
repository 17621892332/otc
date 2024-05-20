package org.orient.otc.common.redisstream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;

import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MyStreamListener implements StreamListener<String, MapRecord<String, String, String>>{
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    RedisStreamTemplate redisStreamTemplate;
    @Autowired(required = false)
    RedisStreamInterface redisStreamInterface;
    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        RecordId messageId = message.getId();
        // 消息的key和value
        Map<String, String> data = message.getValue();
        log.info("组messageId={}, stream={}, body={}", messageId, message.getStream(), data);
        redisStreamInterface.handData(data);
        // 手动确认消费成功
        redisTemplate.opsForStream().acknowledge(redisStreamTemplate.getTopicName(), redisStreamTemplate.getGroup(), message.getId());
    }
}
