package org.orient.otc.system.listener;

import cn.hutool.extra.cglib.CglibUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.common.security.dto.SystemLogInfo;
import org.orient.otc.system.entity.RequestLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;

/**
 * @author dzrh
 */
@RocketMQMessageListener(topic = RocketMqConstant.SYSTEM_LOG
        ,consumerGroup = RocketMqConstant.SYSTEM_LOG
        ,consumeMode= ConsumeMode.CONCURRENTLY)
@Component
@Slf4j
public class SystemLogListener implements RocketMQListener<SystemLogInfo> {
    @Autowired
    private RequestLogMapper requestLogMapper;
    @Override
    public void onMessage(SystemLogInfo messageBody) {
        RequestLog requestLog = CglibUtil.copy(messageBody, RequestLog.class);
        requestLog.setRequestTime(Date.from(messageBody.getRequestTime().atZone(ZoneId.systemDefault()).toInstant()));
        requestLogMapper.save(requestLog);
    }
}
