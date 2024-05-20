package org.orient.otc.common.redisstream;

import lombok.Data;
import org.orient.otc.common.redisstream.enums.StreamTypeEnum;
import org.springframework.stereotype.Component;

@Data
@Component
public class RedisStreamTemplate {

    private String topicName;

    private String group;
    private String consumer;

    /**
     * 每次读取多少数据
     */
    private Integer batchSize = 1;

    private StreamTypeEnum streamType;

    /**
     * 是否打开监听
     */
    private Boolean isOpen = Boolean.TRUE;
}
