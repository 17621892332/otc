//package org.orient.otc.netty.config;
//
//import com.alibaba.fastjson.JSONObject;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import lombok.extern.slf4j.Slf4j;
//import org.orient.otc.netty.component.WebSocketHandler;
//import org.orient.otc.netty.dto.ChannelDTO;
//import org.orient.otc.netty.enums.ChannelType;
//import org.orient.otc.common.redispubsub.RedisPubSubTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//@Component
//@Slf4j
//public class MyRedisPubSubTemplate implements RedisPubSubTemplate {
//
//    @Override
//    public void onMessage(String message) {
//        //收到消息会调用
//        JSONObject pushMsg = new JSONObject();
//        JSONObject jsonObject = JSONObject.parseObject(message);
//        pushMsg.put("InstrumentID", jsonObject.get("InstrumentID"));
//        pushMsg.put("LastPrice", jsonObject.get("LastPrice"));
//        pushMsg.put("UpdateTime", jsonObject.get("UpdateTime"));
//        try {
//            //将收到的消息发送到ctp_md类型的客户端
//            Map<ChannelType, CopyOnWriteArraySet<String>> channelSetByType = WebSocketHandler.getChannelSetByType();
//            CopyOnWriteArraySet<String> s = channelSetByType.get(ChannelType.CTP_MD);
//            Map<String, ChannelDTO> channelMap = WebSocketHandler.getChannelMap();
//            if (Objects.nonNull(s) && !s.isEmpty()) {
//                s.stream().parallel().forEach(a -> {
//                    ChannelDTO channelDto = channelMap.get(a);
//                    if (Objects.nonNull(channelDto) && Objects.nonNull(channelDto.getChannel())) {
//                        channelDto.getChannel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(pushMsg)));
//                    }
//                });
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public String setChannel() {
//        return "ctp_md";
//    }
//}
