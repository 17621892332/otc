package org.orient.otc.common.redispubsub;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
public class SubThread extends Thread{
    private JedisPool jedisPool;

    private String channel;

    private RedisPubSubTemplate redisPubSubTemplate;

    public SubThread(JedisPool jedisPool,String channel,RedisPubSubTemplate redisPubSubTemplate) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel = channel;
        this.redisPubSubTemplate =redisPubSubTemplate;
    }

    @Override
    public void run() {
        System.out.println(String.format("subscribe redis, channel %s, thread will be blocked", channel));
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();   //取出一个连接
            Subscriber subscriber = new Subscriber(redisPubSubTemplate);
            jedis.subscribe(subscriber, channel);    //通过subscribe 的api去订阅，入参是订阅者和频道名
        } catch (Exception e) {
            System.out.println(String.format("subsrcibe channel error, %s", e));
            log.error(e.getMessage(),e);

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
