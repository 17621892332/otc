package org.orient.otc.common.rocketmq.config;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dzrh
 */
@Configuration
public class RocketConfig implements ApplicationContextAware {
    public static final String PRODUCER_BEAN_NAME = "defaultMQProducer";
    public static final String CONSUMER_BEAN_NAME = "defaultLitePullConsumer";
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Bean(destroyMethod = "destroy")
    public RocketMQTemplate rocketMQTemplate(MyMQMessageConverter myMQMessageConverter) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        if (applicationContext.containsBean(PRODUCER_BEAN_NAME)) {
            rocketMQTemplate.setProducer((DefaultMQProducer) applicationContext.getBean(PRODUCER_BEAN_NAME));
        }
        if (applicationContext.containsBean(CONSUMER_BEAN_NAME)) {
            rocketMQTemplate.setConsumer((DefaultLitePullConsumer) applicationContext.getBean(CONSUMER_BEAN_NAME));
        }
        //设置消息转换器，处理LocalDateTime类型
        rocketMQTemplate.setMessageConverter(myMQMessageConverter.getMessageConverter());
        return rocketMQTemplate;
    }
    @Bean
    public MyMQMessageConverter createRocketMQMessageConverter(){
        return new MyMQMessageConverter();
    }
}
