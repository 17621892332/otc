package org.orient.otc.common.redisstream;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.redisstream.enums.StreamTypeEnum;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StreamConsumer {

    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisConnectionFactory redisConnectionFactory;
    @Resource
    RedisStreamTemplate redisStreamTemplate;
    @Resource
    private RedisStreamInterface redisStreamInterface;
    @Resource
    private  StreamListener streamListener;

    @Resource
    private JedisPool jedisPool;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 启动消息监听
     */
    @PostConstruct
    public void run() {
        new Thread(this::rune).start();
    }

    private void rune() {
        if (redisStreamInterface != null) {
            redisStreamInterface.setTemplate(redisStreamTemplate);
            //如果isOpen==false,不启动监听
            if(!redisStreamTemplate.getIsOpen()){
                return;
            }
            try {
                stringRedisTemplate.opsForStream().createGroup(redisStreamTemplate.getTopicName(), ReadOffset.from("0"), redisStreamTemplate.getGroup());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.info("组存在");
            }
            //推的模式
            if(redisStreamTemplate.getStreamType() == StreamTypeEnum.simple) {

                log.info("启动监听器...");
                // 2.消息监听容器选项
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>>
                        containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder().pollTimeout(Duration.ofSeconds(1))
                        .errorHandler(t -> {
                            log.error(t.getMessage(), t);
                            // 异常处理代码
                            log.error("消息处理失败：{}", t.getMessage());
                        })// 序列化器
                        .serializer(new StringRedisSerializer())
                        .executor(threadPoolTaskExecutor).batchSize(redisStreamTemplate.getBatchSize()).build();
                if (handlePending()) {
                    // 3.消息流监听容器
                    StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                            StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions);
                    // 4.注册监听器
                    container.receive(Consumer.from(redisStreamTemplate.getGroup(), redisStreamTemplate.getConsumer()),
                            StreamOffset.create(redisStreamTemplate.getTopicName(), ReadOffset.lastConsumed()),
                            streamListener);
                    // 5.启动消息流监听容器
                    container.start();
                }
            }else {
                //主动拉的模式（为了实现集群模式下的顺序执行，所以加分布式锁）
                if(handlePending()) {
                    while (true) {
                        RLock fairLock = redissonClient.getFairLock("redisStream|" + redisStreamTemplate.getTopicName() + "|" + redisStreamTemplate.getGroup());
                        fairLock.lock();
                        try (Jedis jedis = jedisPool.getResource()) {
                            List<Map.Entry<String, List<StreamEntry>>> entries = jedis.xreadGroup(redisStreamTemplate.getGroup(), redisStreamTemplate.getConsumer(), redisStreamTemplate.getBatchSize(), 10000, false, new MyJedisEntry(redisStreamTemplate.getTopicName(), StreamEntryID.UNRECEIVED_ENTRY));
                            if(entries != null) {
                                for (Map.Entry<String, List<StreamEntry>> entry : entries) {
                                    List<StreamEntry> value = entry.getValue();
                                    for (StreamEntry a : value) {
                                        try {
                                            log.info("组messageId={}, body={}", a.getID(), a.getFields());
                                            redisStreamInterface.handData(a.getFields());
                                            // 手动确认消费成功
                                            jedis.xack(redisStreamTemplate.getTopicName(), redisStreamTemplate.getGroup(), a.getID());
                                        }catch (Exception e){
                                            log.error("redisStream消息处理失败："+JSONObject.toJSONString(a.getFields()),e.getMessage());
                                        }
                                    }
                                }
                            }
                        }catch (Exception e){
                            log.error("redisStream处理消息失败{}",e.getMessage());
                        }finally {
                            fairLock.unlock();
                        }
//                        StreamReadOptions options = StreamReadOptions.empty().block(Duration.ofMillis(10)).count(1000);
//                        List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(Consumer.from(redisStreamTemplate.getGroup(), redisStreamTemplate.getConsumer()), options,
//                                StreamOffset.create(redisStreamTemplate.getTopicName(), ReadOffset.lastConsumed()));
//                        if (list == null || list.isEmpty()) {
//                            continue;
//                        }
//                        for (MapRecord<String, Object, Object> entries : list) {
//                            try {
//                                log.info("组messageId={}, body={}", entries.getId().getValue(), entries.getValue());
//                                stringRedisTemplate.opsForStream().acknowledge(redisStreamTemplate.getTopicName(), redisStreamTemplate.getGroup(), entries.getId());
//                            }catch (Exception e){
//                                log.error("redisStream消息处理失败：" + JSONObject.toJSONString(entries.getValue()), e.getMessage());
//                            }
//                        }
                    }
                }
            }
        }
    }
    private Boolean handlePending(){
        //这个地方用jedis的原因是用spring-data-redis处理有包冲突的问题，比较麻烦
        try(Jedis jedis = jedisPool.getResource()) {
            while (true) {
                List<StreamPendingEntry> xpending = jedis.xpending(redisStreamTemplate.getTopicName(), redisStreamTemplate.getGroup(), null, null, 1, redisStreamTemplate.getConsumer());
                if(null != xpending && !xpending.isEmpty()) {
                    // 通过最前面一条数据的ID，然后查该ID到最后的所有待ACK数据，一次提取10条
                    for (StreamPendingEntry entry : xpending) {
                        List<StreamEntry> streamEntryList = jedis.xrange(redisStreamTemplate.getTopicName(), entry.getID(),null,1);
                        for (StreamEntry a : streamEntryList) {
                            System.out.println("收到待处理数据：" + JSONObject.toJSONString(a));
                            redisStreamInterface.handData(a.getFields());
                            // 确认消息，一旦消费者成功地处理完一条消息，它应该调用XACK，这样这个消息就不会被再次处理
                            // 且作为一个副作用，关于此消息的PEL条目也会被清除，从Redis服务器释放内存
                            jedis.xack(redisStreamTemplate.getTopicName(), redisStreamTemplate.getGroup(), entry.getID());
                        }
//                        log.info("xpending中的数据：{}, {}", entry.getID().toString(),JSONObject.toJSONString(entry));
//                        Map<String,String> map = JSONObject.parseObject(JSONObject.toJSONString(entry), Map.class);
//                        redisStreamInterface.handData(map);
//                        // 确认消息，一旦消费者成功地处理完一条消息，它应该调用XACK，这样这个消息就不会被再次处理
//                        // 且作为一个副作用，关于此消息的PEL条目也会被清除，从Redis服务器释放内存
//                        jedis.xack(redisStreamTemplate.getTopicName(), redisStreamTemplate.getGroup(), entry.getID());
                    }
                }else {
                    return Boolean.TRUE;
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return  Boolean.FALSE;
    }
}


