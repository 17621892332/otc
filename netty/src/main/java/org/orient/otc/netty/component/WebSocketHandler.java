package org.orient.otc.netty.component;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.netty.dto.DeltaAdjustmentDto;
import org.orient.otc.netty.dto.RiskInfoQueryDto;
import org.orient.otc.api.quote.dto.RiskVolUpdateDto;
import org.orient.otc.api.quote.feign.TradeMngClient;
import org.orient.otc.netty.dto.ChannelDTO;
import org.orient.otc.netty.dto.ReceiveMsg;
import org.orient.otc.netty.dto.RiskTimeEditDTO;
import org.orient.otc.netty.enums.ChannelType;
import org.orient.otc.netty.service.RiskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.orient.otc.netty.enums.ChannelType.CTP_MD;

/**
 * socket处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable//保证处理器，在整个生命周期中就是以单例的形式存在，方便统计客户端的在线数量
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Resource
    RiskService riskService;

    @Resource
    TradeMngClient tradeMngClient;
    //所有客户端的通道
    @Getter
    private static final Map<String, ChannelDTO> channelMap = new ConcurrentHashMap<>();
    //每个通道类型下有哪些通道
    //当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行Copy，复制出一个新的容器，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。这样做的好处是我们可以对CopyOnWrite容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。所以CopyOnWrite容器也是一种读写分离的思想，读和写不同的容器。
    @Getter
    private static final Map<ChannelType, CopyOnWriteArraySet<String>> channelSetByType = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        try{
            String key = ctx.channel().id().asLongText();

              //接受客户端发送的消息
            String clientMsg = msg.text();
            log.info("接受客户端的消息......" + ctx.channel().remoteAddress() + "-内容[" + clientMsg + "]");
            ReceiveMsg receiveMsg = JSONObject.parseObject(clientMsg, ReceiveMsg.class);
            if (receiveMsg !=null){
                switch (receiveMsg.getChannelType()){
                    case CTP_MD:
                        ChannelDTO channelDto = new ChannelDTO();
                        channelDto.setChannel(ctx.channel());
                        channelDto.setChannelType(CTP_MD);
                        channelMap.put(key, channelDto);
                        CopyOnWriteArraySet<String> s = channelSetByType.get(CTP_MD);
                        s.add(key);
                        break;
                    case RISK_TOTAL:
                        RiskInfoQueryDto riskInfoQueryDto = JSONObject.parseObject(receiveMsg.getMsgInfo().toString(),RiskInfoQueryDto.class);
                        riskService.modifySearchCriteria(ctx.channel(),riskInfoQueryDto);
                        break;
                    case DELTA_ADJUSTMENT:
                        DeltaAdjustmentDto deltaAdjustmentDto = JSONObject.parseObject(receiveMsg.getMsgInfo().toString(),DeltaAdjustmentDto.class);
                       riskService.editDeltaAdjustment(deltaAdjustmentDto);
                        break;
                    case EDIT_RISK_TIME:
                        RiskTimeEditDTO riskTimeEditDto = JSONObject.parseObject(receiveMsg.getMsgInfo().toString(), RiskTimeEditDTO.class);
                        riskService.editRiskTime(riskTimeEditDto);
                        break;
                    case EDIT_RISK_VOL:
                        RiskVolUpdateDto riskVolUpdateDto = JSONObject.parseObject(receiveMsg.getMsgInfo().toString(), RiskVolUpdateDto.class);
                        tradeMngClient.updateRiskVol(riskVolUpdateDto);
                        break;
                }
            }
        }catch (Exception e){
            log.error("websocket channelRead0发生错误：", e);
        }
    }
    /**
     * 客户端连接时候的操作
     *
     * @param ctx
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.debug("一个客户端连接......" + ctx.channel().remoteAddress() + Thread.currentThread().getName());
        ChannelDTO channelDto = new ChannelDTO();
        channelDto.setChannel(ctx.channel());
        channelMap.put(ctx.channel().id().asLongText(),channelDto);
        ctx.channel().writeAndFlush(new TextWebSocketFrame("连接成功"));
    }


    /**
     * 客户端掉线时的操作
     *
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        String key = ctx.channel().id().asLongText();
        removeKey(key);
        log.debug("一个客户端移除......" + ctx.channel().remoteAddress());
        ctx.close(); //关闭连接
    }

    /**
     * 发生异常时执行的操作
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String key = ctx.channel().id().asLongText();
        removeKey(key);
        //关闭长连接
        ctx.close();
        log.error("异常发生 " + cause.getMessage());
    }

    public void removeKey(String key){
        if(channelMap.containsKey(key)) {
            //移除channelSetByType
            ChannelDTO channelDto = channelMap.get(key);
            if (Objects.nonNull(channelDto)){
                ChannelType channelType = channelDto.getChannelType();
                if(Objects.nonNull(channelType)) {
                    CopyOnWriteArraySet<String> s = channelSetByType.get(channelType);
                    if (Objects.nonNull(s)) {
                        s.remove(key);
                    }
                }
            }
            //移除通信过的channel
            channelMap.remove(key);
        }
    }
}
