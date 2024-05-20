package org.orient.otc.user.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.redisstream.RedisStreamInterface;
import org.orient.otc.common.redisstream.RedisStreamTemplate;
import org.orient.otc.common.redisstream.enums.StreamTypeEnum;
import org.orient.otc.user.exception.BussinessException;
import org.orient.otc.user.vo.ExchangeAccountLoginVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@Component
@Slf4j
public class MyRedisStreamImpl implements RedisStreamInterface {
    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Override
    public void handData(Map<String, String> data) {
        if (data.containsKey("ctpmsg")) {
            String ctpmsg = data.get("ctpmsg");
            JSONObject jsonObject = JSONObject.parseObject(ctpmsg);
            if (jsonObject.containsKey("type")) {
                Integer type = jsonObject.getInteger("type");
                if (type == 1) {
                    ExchangeAccountLoginVO content = JSONObject.parseObject(jsonObject.getString("data"), ExchangeAccountLoginVO.class);
                    stringRedisTemplate.opsForHash().put(RedisAdapter.EXCHANGE_ACCOUNT_LOGIN_STATUS,content.getUserId(),JSONObject.toJSONString(content));
                }
                if(type==-1){
                    ExchangeAccountLoginVO content = JSONObject.parseObject(jsonObject.getString("data"), ExchangeAccountLoginVO.class);
                    stringRedisTemplate.opsForHash().put(RedisAdapter.EXCHANGE_ACCOUNT_LOGIN_STATUS,content.getUserId(),JSONObject.toJSONString(content));
                }
            }
        }
    }


    @Override
    public void setTemplate(RedisStreamTemplate redisStreamTemplate) {
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            BussinessException.E_100101.doThrow();
        }
        redisStreamTemplate.setTopicName("mq_session");
        redisStreamTemplate.setGroup("user");
        redisStreamTemplate.setConsumer(host);
        redisStreamTemplate.setStreamType(StreamTypeEnum.pull);
        redisStreamTemplate.setBatchSize(1);

    }

}
