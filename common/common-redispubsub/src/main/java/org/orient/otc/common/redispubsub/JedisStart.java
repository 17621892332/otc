package org.orient.otc.common.redispubsub;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;


@Slf4j
@Component
public class JedisStart {
    @Value("${jedis.host}")
    private  String jedisHost;
    @Value("${jedis.port}")
    private  Integer jedisPort;
    @Value("${jedis.password:}") //如果配置文件没有jedis.password默认值就为""
    private  String jedisPassword;
    @Autowired
    RedisPubSubTemplate redisPubSubTemplate;
    @PostConstruct
    public void start() {
        try{
            JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), jedisHost, jedisPort,3000, StringUtils.isNotBlank(jedisPassword)?jedisPassword:null);
            SubThread subThread = new SubThread(jedisPool,redisPubSubTemplate.setChannel(),redisPubSubTemplate);  //订阅者
            subThread.start();
        }catch (Exception e){
            log.error("订阅失败：",e);
        }
    }
}
