package org.orient.otc.common.redisstream;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Stream消费接口
 */
public interface RedisStreamInterface {
    /**
     *  监听消息
     * @param data 消息内容
     */
    void handData(Map<String, String> data);

    /**
     * 相同group ，设置不同consumer可以达到负载消费的效果，设置同一个consumer每个consumer会接收到所有的消息，加锁消费避免重复消费可以达到严格按照顺序消费
     * @param redisStreamTemplate 消费模板
     */
    void setTemplate(@Autowired RedisStreamTemplate redisStreamTemplate);
}
