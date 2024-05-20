package org.orient.otc.common.rocketmq.config;

import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

public class MyMQMessageConverter extends RocketMQMessageConverter {

    private static final boolean FASTJSON_PRESENT;

    static {
        ClassLoader classLoader = RocketMQMessageConverter.class.getClassLoader();
        FASTJSON_PRESENT = ClassUtils.isPresent("com.alibaba.fastjson.JSON", classLoader) &&
                ClassUtils.isPresent("com.alibaba.fastjson.support.config.FastJsonConfig", classLoader);
    }

    private final CompositeMessageConverter messageConverter;

    public MyMQMessageConverter() {
        List<MessageConverter> messageConverters = new ArrayList<>();
        ByteArrayMessageConverter byteArrayMessageConverter = new ByteArrayMessageConverter();
        byteArrayMessageConverter.setContentTypeResolver(null);
        messageConverters.add(byteArrayMessageConverter);
        messageConverters.add(new StringMessageConverter());
//        if (JACKSON_PRESENT) {
//            MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//            ObjectMapper mapper = converter.getObjectMapper();
//            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            //LocalDatetime序列化
//            JavaTimeModule timeModule = new JavaTimeModule();
//            timeModule.addDeserializer(LocalDate.class,
//                    new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            timeModule.addDeserializer(LocalDateTime.class,
//                    new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            timeModule.addSerializer(LocalDate.class,
//                    new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            timeModule.addSerializer(LocalDateTime.class,
//                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//            mapper.registerModule(timeModule);
//            converter.setObjectMapper(mapper);
//            messageConverters.add(converter);
//        }
        if (FASTJSON_PRESENT) {
            try {
                messageConverters.add(
                        (MessageConverter)ClassUtils.forName(
                                "com.alibaba.fastjson.support.spring.messaging.MappingFastJsonMessageConverter",
                                ClassUtils.getDefaultClassLoader()).newInstance());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ignored) {
                //ignore this exception
            }
        }
        messageConverter = new CompositeMessageConverter(messageConverters);
    }
    @Override
    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

}
