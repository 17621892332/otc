package org.orient.otc.user.listener;

import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MQServiceTest extends TestCase {

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Test
    public void sendMsg() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uri","ceshi");
        rocketMQTemplate.syncSend(RocketMqConstant.SYSTEM_LOG,jsonObject);

    }
}
