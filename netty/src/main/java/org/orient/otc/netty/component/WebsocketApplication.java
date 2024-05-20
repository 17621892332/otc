package org.orient.otc.netty.component;

import lombok.extern.slf4j.Slf4j;
import org.orient.otc.netty.dto.ChannelDTO;
import org.orient.otc.netty.enums.ChannelType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.orient.otc.netty.enums.ChannelType.getChannelTypeMap;

@Slf4j
@Component("WebsocketApplication")
public class WebsocketApplication implements Runnable{
    @Resource
    private WebsocketInitialization websocketInitialization;

  @Override
    public void run() {
        try {
            log.info(Thread.currentThread().getName() + ":websocket启动中......");
            //初始化channelSetByType,避免启动后大量client连接上再创建CopyOnWriteArraySet导致的数据安全问题
            Map<String, ChannelType> channelTypeMap = getChannelTypeMap();
            channelTypeMap.forEach((k,v)->{
                WebSocketHandler.getChannelSetByType().put(v,new CopyOnWriteArraySet<>());
            });

            // 定时任务删除缓存中失效的channel
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Map<String, ChannelDTO> channelMap = WebSocketHandler.getChannelMap();
                    channelMap.forEach((k,v) -> {
                        try {
                            if (Objects.nonNull(v.getChannel()) && !v.getChannel().isActive()) {
                                channelMap.remove(v.getChannel().id().asLongText());
                            }
                        }catch (Exception e){
                            log.error("删除缓存中的channel失败",e);
                        }
                    });
                    Map<ChannelType, CopyOnWriteArraySet<String>> channelSetByType = WebSocketHandler.getChannelSetByType();
                    channelSetByType.forEach((k,v) -> {
                        if(Objects.nonNull(v) && v.size() > 0){
                            v.forEach(channelId -> {
                                try {
                                    if (!WebSocketHandler.getChannelMap().containsKey(channelId)) {
                                        v.remove(channelId);
                                    }
                                }catch (Exception e){
                                    log.error("删除缓存中的channel失败",e);
                                }
                            });
                        }
                    });
                }
            };
            // 计时器
            Timer timer = new Timer();
            // 开始执行任务 (延迟1000毫秒执行，每10分钟执行一次)
            timer.schedule(timerTask, 1000, 1000*60);

            websocketInitialization.init();
            log.info(Thread.currentThread().getName() + ":websocket启动成功！！！");
        } catch (Exception e) {
            log.error("websocket发生错误：",e);
        }
    }
}
