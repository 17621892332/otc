package org.orient.otc.common.cache.config;

import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RedissionConfig
 */
@Configuration
public class RedissionConfig {
    @Value("${spring.redis.host}")
    private  String redisHost;
    @Value("${spring.redis.password}")
    private  String redisPassword;
    @Value("${spring.redis.port}")
    private  String redisPort;
    @Value("${spring.redis.database}")
    private  int redisDatabase;
    @Bean(destroyMethod = "shutdown")
    RedissonClient redission() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+redisHost+":"+redisPort)
                .setPassword(StringUtils.isNotBlank(redisPassword)?redisPassword:null)
                .setDatabase(redisDatabase);
        return Redisson.create(config);
    }

}
