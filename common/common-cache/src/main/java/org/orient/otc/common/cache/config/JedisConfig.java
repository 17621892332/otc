package org.orient.otc.common.cache.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * JedisConfig
 */
@Configuration
public class JedisConfig {
    @Value("${jedis.host}")
    private  String jedisHost;
    @Value("${jedis.port}")
    private  Integer jedisPort;
    @Value("${jedis.password:}") //如果配置文件没有jedis.password默认值就为""
    private  String jedisPassword;
    @Bean
    JedisPool jedisPool() {
        return new JedisPool(new JedisPoolConfig(), jedisHost, jedisPort,3000, StringUtils.isNotBlank(jedisPassword)?jedisPassword:null);
    }

}
